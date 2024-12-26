package cn.cimems.schedule.repository;

import cn.cimems.schedule.model.Device;
import cn.cimems.schedule.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reservation r " +
           "WHERE r.device = :device " +
           "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
    boolean existsByDeviceAndTimeConflict(
            @Param("device") Device device,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    List<Reservation> findByDevice(Device device);
}
