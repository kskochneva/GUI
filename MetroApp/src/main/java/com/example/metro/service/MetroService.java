package com.example.metro.service;

import com.example.metro.model.Station;
import com.example.metro.model.Line;
import com.example.metro.model.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MetroService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Station> getAllStations() {
        String sql = "SELECT id, name, line_id, x, y FROM stations ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Station s = new Station();
            s.id = rs.getInt("id");
            s.name = rs.getString("name");
            s.line_id = rs.getInt("line_id");
            s.pos_x = rs.getDouble("x");
            s.pos_y = rs.getDouble("y");
            return s;
        });
    }

    public List<Line> getAllLines() {
        String sql = "SELECT id, name, color FROM lines ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Line l = new Line();
            l.id = rs.getInt("id");
            l.name = rs.getString("name");
            l.color_hex = rs.getString("color");
            return l;
        });
    }

    public List<Connection> getAllConnections() {
        String sql = "SELECT id, station1_id, station2_id, travel_time FROM connections ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Connection c = new Connection();
            c.id = rs.getInt("id");
            c.station_from = rs.getInt("station1_id");
            c.station_to = rs.getInt("station2_id");
            c.travel_time = rs.getInt("travel_time");
            c.is_transfer = false;
            return c;
        });
    }

    public Station getStationById(int id) {
        String sql = "SELECT id, name, line_id, x, y FROM stations WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Station s = new Station();
            s.id = rs.getInt("id");
            s.name = rs.getString("name");
            s.line_id = rs.getInt("line_id");
            s.pos_x = rs.getDouble("x");
            s.pos_y = rs.getDouble("y");
            return s;
        }, id);
    }
}
