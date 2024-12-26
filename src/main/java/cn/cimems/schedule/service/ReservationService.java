package cn.cimems.schedule.service;

import cn.cimems.schedule.model.Device;
import cn.cimems.schedule.model.Reservation;
import cn.cimems.schedule.model.User;
import cn.cimems.schedule.repository.ReservationRepository;
import cn.cimems.schedule.repository.DeviceRepository;
import cn.cimems.schedule.security.CustomUserDetails;
import cn.cimems.schedule.payload.ReservationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * 创建新的预约记录
     *
     * @param request 预约请求
     * @return 新的预约记录
     */
    public Reservation createReservation(ReservationRequest request) {
        // 检查设备是否存在
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new RuntimeException("Device not found."));

        // 检查时间冲突
        boolean hasConflict = reservationRepository.existsByDeviceAndTimeConflict(
                device, request.getStartTime(), request.getEndTime());
        if (hasConflict) {
            throw new RuntimeException("Time conflict with an existing reservation.");
        }

        // 创建新的预约记录
        Reservation reservation = new Reservation();
        reservation.setUser(getCurrentUser());
        reservation.setDevice(device);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());

        return reservationRepository.save(reservation);
    }

    /**
     * 取消预约
     *
     * @param reservationId 预约ID
     */
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found."));

        // 检查权限：普通用户只能取消自己的预约
        User currentUser = getCurrentUser();
        if (!reservation.getUser().equals(currentUser) && !isAdminOrSuperAdmin(currentUser)) {
            throw new RuntimeException("You do not have permission to cancel this reservation.");
        }

        reservationRepository.delete(reservation);
    }

    /**
     * 获取设备的所有预约记录
     *
     * @param deviceId 设备ID
     * @return 预约列表
     */
    public List<Reservation> getReservationsForDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found."));
        return reservationRepository.findByDevice(device);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }

    private boolean isAdminOrSuperAdmin(User user) {
        return user.getRole().equals("ADMIN") || user.getRole().equals("SUPER_ADMIN");
    }
}
