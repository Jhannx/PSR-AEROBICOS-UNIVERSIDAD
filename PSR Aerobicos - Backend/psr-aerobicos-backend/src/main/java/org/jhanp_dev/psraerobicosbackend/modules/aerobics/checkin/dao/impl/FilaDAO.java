package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces.IFilaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaEsperaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaRecepcionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.mapper.FilaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FilaDAO implements IFilaDAO {
    private final Connection conexion;
    private final FilaMapper mapper;

    public FilaDAO(FilaMapper mapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.mapper = mapper;
    }

    @Override
    public List<FilaRecepcionResponseDTO> listarFilaRecepcion() {
        String sql = "SELECT * FROM vista_fila_hoy ORDER BY hora_llegada ASC";
        List<FilaRecepcionResponseDTO> resultado = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapper.aRecepcion(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar la fila de recepción: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FilaEsperaResponseDTO> listarFilaEspera() {
        String sql = "SELECT * FROM vista_fila_espera ORDER BY hora_llegada ASC";
        List<FilaEsperaResponseDTO> resultado = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapper.aEspera(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar la fila de espera: " + e.getMessage(), e);
        }
    }
}
