package cn.cimems.schedule.controller;

import cn.cimems.schedule.model.Reservation;
import cn.cimems.schedule.service.ReservationService;
import cn.cimems.schedule.payload.ReservationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 创建新的预约
     *
     * @param request 预约请求数据
     * @return 新的预约记录
     */
    @PostMapping
    @ResponseBody
    public Reservation createReservation(@RequestBody ReservationRequest request) {
        return reservationService.createReservation(request);
    }

    /**
     * 取消预约
     *
     * @param id 预约的ID
     * @return 成功取消的消息
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public String cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return "Reservation canceled successfully";
    }

    /**
     * 获取设备的所有预约记录
     *
     * @param deviceId 设备ID
     * @return 设备预约记录列表
     */
    @GetMapping("/device/{deviceId}")
    @ResponseBody
    public List<Reservation> getReservationsForDevice(@PathVariable Long deviceId) {
        return reservationService.getReservationsForDevice(deviceId);
    }
}
