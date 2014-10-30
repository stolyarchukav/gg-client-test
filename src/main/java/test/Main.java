package test;

import com.sun.management.UnixOperatingSystemMXBean;
import org.gridgain.client.*;
import org.gridgain.client.balancer.GridClientRoundRobinBalancer;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Main {

    public static void main(String[] args) throws IOException {
        String addressStr = args[0];
        String[] addressesArray = addressStr.split(",");
        Collection<String> addresses = new ArrayList<>();
        for (String address : addressesArray) {
            addresses.add(address + ":11211");
        }

        GridClientConfiguration cfg = getGridClientConfiguration(addresses);

        try (GridClient client = GridClientFactory.start(cfg)) {
            while (true) {
                System.out.println("Connected: " + client.connected());
                OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
                if (os instanceof UnixOperatingSystemMXBean){
                    System.out.println("Number of open fd: " +
                            ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount());
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (GridClientException e) {
            e.printStackTrace();
        }
    }

    public static GridClientConfiguration getGridClientConfiguration(Collection<String> addresses) {
        GridClientConfiguration cfg = new GridClientConfiguration();
        GridClientDataConfiguration cache = new GridClientDataConfiguration();
        cache.setName("test");
        cfg.setDataConfigurations(Arrays.asList(cache));
        cfg.setServers(addresses);
        cfg.setBalancer(new GridClientRoundRobinBalancer());
        cfg.setProtocol(GridClientProtocol.TCP);
        return cfg;
    }

}
