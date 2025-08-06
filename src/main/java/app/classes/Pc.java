package app.classes;

import app.objects.objGlobals;
import app.objects.objLogTimeline;
import com.sun.management.OperatingSystemMXBean;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;

public class Pc {
    private static long startTime=0;
    private static long startTransferTime=0;
    public static ConcurrentHashMap<String, Double> usage = new ConcurrentHashMap<>();

    public static void cpu(){
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double systemCpuLoad = osBean.getCpuLoad() * 100;
        objLogTimeline.add("cpuUsage","System CPU Load: "+systemCpuLoad);
        usage.put("cpu", systemCpuLoad);
    }

    public static void disk() {
        SystemInfo systemInfo = new SystemInfo();
        HWDiskStore[] diskStores = systemInfo.getHardware().getDiskStores().toArray(new HWDiskStore[0]);

        for (HWDiskStore disk : diskStores) {
            HWPartition[] partitions = disk.getPartitions().toArray(new HWPartition[0]);

            for (HWPartition partition : partitions) {
                if (partition.getMountPoint() != null && partition.getMountPoint().equalsIgnoreCase(objGlobals.partition+ ":\\")) {
                    if(startTime==0){
                        startTime=disk.getTimeStamp();
                        startTransferTime=disk.getTransferTime();
                    }
                    else{
                        long endTime = disk.getTimeStamp();
                        long endTransferTime = disk.getTransferTime();
                        long deltaBusy = endTransferTime - startTransferTime;
                        long deltaTime = endTime - startTime;
                        double busyPercentage = 0;
                        if (deltaTime > 0) {
                            busyPercentage = (deltaBusy * 100.0) / deltaTime;
                        }
                        usage.put("disk", busyPercentage);
                        startTime=startTransferTime=0;
                        objLogTimeline.add("diskUsage","diskUsage : "+ busyPercentage);
                    }
                    break;
                }
            }
        }
    }

}
