
import java.net.*;
import java.nio.charset.StandardCharsets;

public class b65ibdTE {
    public static void main(String[] args) {
        String serverIP = "36.50.135.242";
        int serverPort = 2207;
        String studentCode = "B23DCCN951";
        String questionCode = "b65ibdTE";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);
            InetAddress address = InetAddress.getByName(serverIP);

            byte[] request = (";" + studentCode + ";" + questionCode)
                    .getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(request, request.length, address, serverPort));

            byte[] buffer = new byte[8192];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String response = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
            int separator = response.indexOf(';');
            if (separator < 0) throw new IllegalArgumentException("Sai dinh dang phan hoi");

            String requestId = response.substring(0, separator);
            String[] parts = response.substring(separator + 1).trim().split(",");

            if (parts.length == 0) throw new IllegalArgumentException("Khong co du lieu");

            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;

            for (String part : parts) {
                int number = Integer.parseInt(part.trim());
                max = Math.max(max, number);
                min = Math.min(min, number);
            }

            String result = requestId + ";" + max + "," + min;
            byte[] answer = result.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(answer, answer.length, packet.getAddress(), packet.getPort()));

            System.out.println("So luong nhan duoc: " + parts.length);
            System.out.println("Da gui: " + result);
        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
