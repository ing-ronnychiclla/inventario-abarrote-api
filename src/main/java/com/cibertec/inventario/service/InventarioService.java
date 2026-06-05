package com.cibertec.inventario.service;

import com.cibertec.inventario.dto.IngresoMercanciaRequestDTO;
import com.cibertec.inventario.dto.IngresoMercanciaResponseDTO;
import com.cibertec.inventario.dto.VentaRequestDTO;
import com.cibertec.inventario.dto.VentaResponseDTO;
import com.cibertec.inventario.entity.Kardex;
import com.cibertec.inventario.entity.Lote;
import com.cibertec.inventario.entity.Producto;
import com.cibertec.inventario.entity.Proveedor;
import com.cibertec.inventario.repository.KardexRepository;
import com.cibertec.inventario.repository.LoteRepository;
import com.cibertec.inventario.repository.ProductoRepository;
import com.cibertec.inventario.repository.ProveedorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final LoteRepository loteRepository;
    private final KardexRepository kardexRepository;

    @Transactional
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

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRequestDTO request) {
        // 1. Validar que el producto exista
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // 2. Obtener todos los lotes con stock disponible de este producto ordenados por vencimiento (FIFO)
        List<Lote> lotesDisponibles = loteRepository.findLotesConStockPorProducto(producto.getId());

        // 3. Calcular el stock total sumando lo que queda en cada lote
        int stockTotal = lotesDisponibles.stream().mapToInt(Lote::getCantidadActual).sum();

        // 4. Si el cliente pide mas de lo que hay en toda la tienda, rechazar la venta
        if (request.cantidad() > stockTotal) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre()
                    + ". Stock actual total: " + stockTotal);
        }

        int cantidadPorDespachar = request.cantidad();

        // 5. Bucle Algoritmo FIFO
        for (Lote lote : lotesDisponibles) {
            if (cantidadPorDespachar == 0) break; // Ya despachamos todo lo solicitado

            int stockDisponibleEnLote = lote.getCantidadActual();

            if (stockDisponibleEnLote >= cantidadPorDespachar) {
                // Caso A: El lote actual tiene suficiente o mas de lo que falta despachar
                lote.setCantidadActual(stockDisponibleEnLote - cantidadPorDespachar);

                // Registramos el movimiento en el Kardex amarrado a este lote
                registrarMovimientoKardex(producto, lote, Kardex.TipoMovimiento.SALIDA_VENTA, cantidadPorDespachar, "Venta en caja");

                cantidadPorDespachar = 0; // Se completo la venta
            } else {
                // Caso B: El lote no alcanza por completo. Lo vaciamos a 0 y pasamos al siguiente lote
                lote.setCantidadActual(0);

                registrarMovimientoKardex(producto, lote, Kardex.TipoMovimiento.SALIDA_VENTA, stockDisponibleEnLote, "Venta en caja (Lote agotado)");

                cantidadPorDespachar -= stockDisponibleEnLote; // Restamos lo que ya pudimos despachar de este lote
            }

                // Guardamos el estado del lote modificado
            loteRepository.save(lote);
        }

        // 6. Calcular el total a pagar (Cantidad vendida * precio de venta del catalogo)
        BigDecimal totalPagar = producto.getPrecioVenta().multiply(BigDecimal.valueOf(request.cantidad()));

        return new VentaResponseDTO(
                producto.getId(),
                producto.getNombre(),
                request.cantidad(),
                totalPagar,
                LocalDateTime.now()
        );

        }

    // Metodo auxiliar interno para no repetir codigo de insercion en el Kardex
    private void registrarMovimientoKardex(Producto producto, Lote lote, Kardex.TipoMovimiento tipo, int cantidad, String motivo) {
        Kardex kardex = new Kardex();
        kardex.setProducto(producto);
        kardex.setLote(lote);
        kardex.setTipoMovimiento(tipo);
        kardex.setCantidad(cantidad);
        kardex.setMotivo(motivo);
        kardexRepository.save(kardex);
    }
}
