package cn.cimems.schedule.repository;

import cn.cimems.schedule.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 数据访问层接口，用于操作设备表。
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
