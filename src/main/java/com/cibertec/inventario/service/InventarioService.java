package com.cibertec.inventario.service;

import com.cibertec.inventario.dto.*;
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
import java.util.UUID;

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

        // 4. Retornar la confirmación empaquetada en el Resposne DTO
        return new IngresoMercanciaResponseDTO(
                kardexGuardado.getId(),
                loteGuardado.getId(),
                producto.getNombre(),
                loteGuardado.getProveedor() != null ? loteGuardado.getProveedor().getRazonSocial() : "Sin Proveedor",
                loteGuardado.getCantidadInicial(),
                loteGuardado.getCostoUnitario(),
                kardexGuardado.getFechaMovimiento(),
                loteGuardado.getCantidadInicial(),
                loteGuardado.getCantidadActual(),
                loteGuardado.getFechaVencimiento()
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

    public List<KardexResponseDTO> obtenerKardex() {
        return kardexRepository.findAllByOrderByFechaMovimientoDesc().stream()
                .map(k -> new KardexResponseDTO(
                        k.getId(),
                        k.getProducto().getId(),
                        k.getProducto().getNombre(),
                        k.getLote() != null ? k.getLote().getId() : null,
                        k.getTipoMovimiento(),
                        k.getCantidad(),
                        k.getLote() != null ? k.getLote().getCostoUnitario() : null,
                        k.getMotivo(),
                        k.getLote() != null ? k.getLote().getFechaVencimiento() : null,
                        k.getFechaMovimiento()
                ))
                .toList();
    }

    public List<IngresoMercanciaResponseDTO> obtenerIngresos() {
        return kardexRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(Kardex.TipoMovimiento.ENTRADA).stream()
                .map(k -> new IngresoMercanciaResponseDTO(
                        k.getId(),
                        k.getLote() != null ? k.getLote().getId() : null,
                        k.getProducto().getNombre(),
                        k.getLote() != null && k.getLote().getProveedor() != null ? k.getLote().getProveedor().getRazonSocial() : "Sin Proveedor",
                        k.getCantidad(),
                        k.getLote() != null ? k.getLote().getCostoUnitario() : BigDecimal.ZERO,
                        k.getFechaMovimiento(),
                        k.getLote() != null ? k.getLote().getCantidadInicial() : k.getCantidad(),
                        k.getLote() != null ? k.getLote().getCantidadActual() : 0,
                        k.getLote() != null ? k.getLote().getFechaVencimiento() : null
                ))
                .toList();
    }

    public List<VentaResponseDTO> obtenerVentas() {
        return kardexRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(Kardex.TipoMovimiento.SALIDA_VENTA).stream()
                .map(k -> new VentaResponseDTO(
                        k.getProducto().getId(),
                        k.getProducto().getNombre(),
                        k.getCantidad(),
                        k.getProducto().getPrecioVenta().multiply(BigDecimal.valueOf(k.getCantidad())),
                        k.getFechaMovimiento()
                ))
                .toList();
    }

    @Transactional
    public void registrarMovimientoManual(ManualMovimientoRequestDTO request) {
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (request.tipoMovimiento() == Kardex.TipoMovimiento.SALIDA_MERMA) {
            // FIFO para merma
            List<Lote> lotesDisponibles = loteRepository.findLotesConStockPorProducto(producto.getId());
            int stockTotal = lotesDisponibles.stream().mapToInt(Lote::getCantidadActual).sum();

            if (request.cantidad() > stockTotal) {
                throw new IllegalArgumentException("Stock insuficiente para registrar la merma del producto: " 
                        + producto.getNombre() + ". Stock disponible: " + stockTotal);
            }

            int cantidadPorDespachar = request.cantidad();
            for (Lote lote : lotesDisponibles) {
                if (cantidadPorDespachar == 0) break;

                int stockDisponible = lote.getCantidadActual();
                if (stockDisponible >= cantidadPorDespachar) {
                    lote.setCantidadActual(stockDisponible - cantidadPorDespachar);
                    registrarMovimientoKardex(producto, lote, Kardex.TipoMovimiento.SALIDA_MERMA, cantidadPorDespachar, request.motivo());
                    cantidadPorDespachar = 0;
                } else {
                    lote.setCantidadActual(0);
                    registrarMovimientoKardex(producto, lote, Kardex.TipoMovimiento.SALIDA_MERMA, stockDisponible, request.motivo() + " (Lote agotado)");
                    cantidadPorDespachar -= stockDisponible;
                }
                loteRepository.save(lote);
            }
        } else {
            // AJUSTE: Mapeo general
            registrarMovimientoKardex(producto, null, request.tipoMovimiento(), request.cantidad(), request.motivo());
        }
    }

    @Transactional
    public void eliminarIngreso(UUID kardexId) {
        Kardex kardex = kardexRepository.findById(kardexId)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));

        if (kardex.getTipoMovimiento() != Kardex.TipoMovimiento.ENTRADA) {
            throw new IllegalArgumentException("El movimiento no corresponde a un ingreso de mercancía");
        }

        Lote lote = kardex.getLote();
        if (lote != null) {
            if (lote.getCantidadActual() < lote.getCantidadInicial()) {
                throw new IllegalArgumentException("No se puede eliminar el ingreso porque el lote ya registra ventas o movimientos de salida");
            }
            kardexRepository.delete(kardex);
            loteRepository.delete(lote);
        } else {
            kardexRepository.delete(kardex);
        }
    }

    @Transactional
    public IngresoMercanciaResponseDTO actualizarIngreso(UUID kardexId, IngresoMercanciaRequestDTO request) {
        Kardex kardex = kardexRepository.findById(kardexId)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));

        if (kardex.getTipoMovimiento() != Kardex.TipoMovimiento.ENTRADA) {
            throw new IllegalArgumentException("El movimiento no corresponde a un ingreso de mercancía");
        }

        Lote lote = kardex.getLote();
        if (lote == null) {
            throw new IllegalArgumentException("No se encontró el lote asociado al ingreso");
        }

        boolean haTenidoVentas = lote.getCantidadActual() < lote.getCantidadInicial();

        if (haTenidoVentas) {
            if (!lote.getProducto().getId().equals(request.productoId())) {
                throw new IllegalArgumentException("No se puede cambiar el producto de un ingreso que ya registra ventas o movimientos de salida");
            }
            if (!lote.getCantidadInicial().equals(request.cantidad())) {
                throw new IllegalArgumentException("No se puede cambiar la cantidad de un ingreso que ya registra ventas o movimientos de salida");
            }
        } else {
            if (!lote.getProducto().getId().equals(request.productoId())) {
                Producto nuevoProducto = productoRepository.findById(request.productoId())
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                lote.setProducto(nuevoProducto);
                kardex.setProducto(nuevoProducto);
            }
            lote.setCantidadInicial(request.cantidad());
            lote.setCantidadActual(request.cantidad());
            kardex.setCantidad(request.cantidad());
        }

        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        lote.setProveedor(proveedor);
        lote.setCostoUnitario(request.costoUnitario());
        lote.setFechaVencimiento(request.fechaVencimiento());

        Lote loteGuardado = loteRepository.save(lote);
        Kardex kardexGuardado = kardexRepository.save(kardex);

        return new IngresoMercanciaResponseDTO(
                kardexGuardado.getId(),
                loteGuardado.getId(),
                loteGuardado.getProducto().getNombre(),
                loteGuardado.getProveedor() != null ? loteGuardado.getProveedor().getRazonSocial() : "Sin Proveedor",
                loteGuardado.getCantidadInicial(),
                loteGuardado.getCostoUnitario(),
                kardexGuardado.getFechaMovimiento(),
                loteGuardado.getCantidadInicial(),
                loteGuardado.getCantidadActual(),
                loteGuardado.getFechaVencimiento()
        );
    }
}
