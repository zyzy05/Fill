import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP_STRING {
    public static void main(String[] args) throws Exception {
        String serverIP = "36.50.135.242";
        int serverPort = 2208;
        String studentCode = "B23DCCN951";
        String questionCode = "dOfT0sab";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);
            InetAddress address = InetAddress.getByName(serverIP);

            // 1. GUI YEU CAU
            String request = ";" + studentCode + ";" + questionCode;
            byte[] sendData = request.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                sendData, sendData.length, address, serverPort
            ));

            // 2. NHAN DU LIEU
            byte[] buffer = new byte[8192];
            DatagramPacket packet =
                new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String response = new String(
                packet.getData(), 0, packet.getLength(),
                StandardCharsets.UTF_8
            );

            // 3. TACH REQUEST ID VA CHUOI
            int pos = response.indexOf(';');
            String requestId = response.substring(0, pos);
            String data = response.substring(pos + 1);

            // Chi bo khoang trang phan giao thuc neu can.
            if (data.startsWith(" ")) {
                data = data.substring(1);
            }

            // 4. XU LY CHUOI
            // TODO: Viet thuat toan xu ly chuoi
            String resultData = data;

            // 5. DONG GOI KET QUA
            String result = requestId + ";" + resultData;
            byte[] resultBytes =
                result.getBytes(StandardCharsets.UTF_8);

            // 6. GUI LAI SERVER
            socket.send(new DatagramPacket(
                resultBytes, resultBytes.length,
                packet.getAddress(), packet.getPort()
            ));

            System.out.println(result);
        }
    }
}