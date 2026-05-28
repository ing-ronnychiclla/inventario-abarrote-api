package com.cibertec.inventario.service;

import com.cibertec.inventario.dto.IngresoMercanciaRequestDTO;
import com.cibertec.inventario.dto.IngresoMercanciaResponseDTO;
import com.cibertec.inventario.entity.Kardex;
import com.cibertec.inventario.entity.Lote;
import com.cibertec.inventario.entity.Producto;
import com.cibertec.inventario.entity.Proveedor;
import com.cibertec.inventario.repository.KardexRepository;
import com.cibertec.inventario.repository.LoteRepository;
import com.cibertec.inventario.repository.ProductoRepository;
import com.cibertec.inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final LoteRepository loteRepository;
    private final KardexRepository kardexRepository;

    public IngresoMercanciaResponseDTO registrarIngreso(IngresoMercanciaRequestDTO request) {
        // 1. Validar que el Producto y el Proveedor existan realmente en la BD
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        // 2. Crear y guardar el Lote (Las existencias fisicas)
        Lote lote = new Lote();
        lote.setProducto(producto);
        lote.setProveedor(proveedor);
        lote.setFechaVencimiento(request.fechaVencimiento());
        lote.setCostoUnitario(request.costoUnitario());
        lote.setCantidadInicial(request.cantidad());
        // Clave: Al recien ingresar, la cantidad actual es identica a la inicial
        lote.setCantidadActual(request.cantidad());

        Lote loteGuardado = loteRepository.save(lote);

        // 3. Crear y guardar el Kardex (El registro historico)
        Kardex kardex = new Kardex();
        kardex.setProducto(producto);
        kardex.setLote(loteGuardado); // Vinculamos la entrada con el lote exacto
        kardex.setTipoMovimiento(Kardex.TipoMovimiento.ENTRADA);
        kardex.setCantidad(request.cantidad());

        Kardex kardexGuardado = kardexRepository.save(kardex);

        // 4. Retornar la confirmacion empaquetada en el Resposne DTO
        return new IngresoMercanciaResponseDTO(
                kardexGuardado.getId(),
                loteGuardado.getId(),
                producto.getNombre(),
                loteGuardado.getCantidadInicial(),
                loteGuardado.getCostoUnitario(),
                kardexGuardado.getFechaMovimiento()
        );
    }
}
