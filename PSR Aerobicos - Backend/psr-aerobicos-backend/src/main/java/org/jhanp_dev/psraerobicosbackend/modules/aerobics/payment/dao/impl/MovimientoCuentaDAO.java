package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dao.interfaces.IMovimientoCuentaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.ActualizarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.RegistrarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.entity.MovimientoCuenta;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.MetodoPago;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.TipoMovimiento;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.mapper.MovimientoCuentaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MovimientoCuentaDAO implements IMovimientoCuentaDAO {

    private final MovimientoCuentaMapper movimientoCuentaMapper;

    public MovimientoCuentaDAO(MovimientoCuentaMapper movimientoCuentaMapper) {
        this.movimientoCuentaMapper = movimientoCuentaMapper;
    }

    private static final String BASE_SELECT = """
            SELECT id, persona_id, llegada_id, monto_recibido, monto_abonado,
                   metodo_pago, tipo_movimiento, vuelto_entregado, fecha_hora, usuario_id, nota
            FROM movimiento_cuenta
            """;

    @Override
    public List<MovimientoCuentaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY fecha_hora DESC";
        List<MovimientoCuentaResponseDTO> movimientos = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MovimientoCuenta entidad = mapearFila(rs);
                movimientos.add(movimientoCuentaMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar movimientos de cuenta: " + e.getMessage(), e);
        }
        return movimientos;
    }

    @Override
    public List<MovimientoCuentaResponseDTO> listarPorPersonaId(Integer personaId) {
        String sql = BASE_SELECT + " WHERE persona_id = ? ORDER BY fecha_hora DESC";
        List<MovimientoCuentaResponseDTO> movimientos = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MovimientoCuenta entidad = mapearFila(rs);
                    movimientos.add(movimientoCuentaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar movimientos por persona_id: " + e.getMessage(), e);
        }
        return movimientos;
    }

    @Override
    public List<MovimientoCuentaResponseDTO> listarPorLlegadaId(Integer llegadaId) {
        String sql = BASE_SELECT + " WHERE llegada_id = ? ORDER BY fecha_hora DESC";
        List<MovimientoCuentaResponseDTO> movimientos = new ArrayList<>();
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, llegadaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) movimientos.add(movimientoCuentaMapper.aResponseDTO(mapearFila(rs)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar movimientos por llegada: " + e.getMessage(), e);
        }
        return movimientos;
    }

    @Override
    public Optional<MovimientoCuentaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MovimientoCuenta entidad = mapearFila(rs);
                    return Optional.of(movimientoCuentaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener movimiento de cuenta por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public MovimientoCuentaResponseDTO guardar(RegistrarMovimientoCuentaRequestDTO dto) {
        MovimientoCuenta movimiento = movimientoCuentaMapper.aEntidad(dto);
        String sql = "{CALL sp_registrar_movimiento(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setInt(1, movimiento.getPersonaId());
            if (movimiento.getLlegadaId() != null) {
                stmt.setInt(2, movimiento.getLlegadaId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setBigDecimal(3, movimiento.getMontoRecibido());
            stmt.setBigDecimal(4, movimiento.getMontoAbonado());
            stmt.setString(5, movimiento.getMetodoPago() == null ? MetodoPago.EFECTIVO.name() : movimiento.getMetodoPago().name());
            if (movimiento.getTipoMovimiento() != null) {
                stmt.setString(6, movimiento.getTipoMovimiento().name());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            if (movimiento.getVueltoEntregado() != null) {
                stmt.setBoolean(7, movimiento.getVueltoEntregado());
            } else {
                stmt.setNull(7, Types.BOOLEAN);
            }
            stmt.setInt(8, movimiento.getUsuarioId());
            stmt.setString(9, movimiento.getNota());
            stmt.registerOutParameter(10, Types.INTEGER);
            stmt.execute();
            movimiento.setId(stmt.getInt(10));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar movimiento de cuenta: " + e.getMessage(), e);
        }
        return obtenerPorId(movimiento.getId()).orElseThrow(() -> new IllegalStateException("No se encontró el movimiento registrado"));
    }

    @Override
    public boolean actualizar(Integer id, ActualizarMovimientoCuentaRequestDTO dto) {
        MovimientoCuenta movimiento = movimientoCuentaMapper.aEntidad(dto);
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_actualizar_movimiento(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            stmt.setInt(1, id);
            stmt.setInt(2, movimiento.getPersonaId());
            if (movimiento.getLlegadaId() != null) {
                stmt.setInt(3, movimiento.getLlegadaId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setBigDecimal(4, movimiento.getMontoRecibido());
            stmt.setBigDecimal(5, movimiento.getMontoAbonado());
            stmt.setString(6, movimiento.getMetodoPago() == null ? null : movimiento.getMetodoPago().name());
            stmt.setString(7, movimiento.getTipoMovimiento() == null ? null : movimiento.getTipoMovimiento().name());
            stmt.setObject(8, movimiento.getVueltoEntregado(), Types.BOOLEAN);
            stmt.setInt(9, movimiento.getUsuarioId());
            if (movimiento.getNota() != null) {
                stmt.setString(10, movimiento.getNota());
            } else {
                stmt.setNull(10, Types.VARCHAR);
            }
            stmt.registerOutParameter(11, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(11);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar movimiento de cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_eliminar_movimiento(?, ?)}")) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(2);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar movimiento de cuenta: " + e.getMessage(), e);
        }
    }

    private MovimientoCuenta mapearFila(ResultSet rs) throws SQLException {
        int llegadaRaw = rs.getInt("llegada_id");
        Integer llegadaId = rs.wasNull() ? null : llegadaRaw;
        Timestamp fechaHora = rs.getTimestamp("fecha_hora");

        return MovimientoCuenta.builder()
                .id(rs.getInt("id"))
                .personaId(rs.getInt("persona_id"))
                .llegadaId(llegadaId)
                .montoRecibido(rs.getBigDecimal("monto_recibido"))
                .montoAbonado(rs.getBigDecimal("monto_abonado"))
                .metodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")))
                .tipoMovimiento(TipoMovimiento.valueOf(rs.getString("tipo_movimiento")))
                .vueltoEntregado(rs.getBoolean("vuelto_entregado"))
                .fechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null)
                .usuarioId(rs.getInt("usuario_id"))
                .nota(rs.getString("nota"))
                .build();
    }
}
