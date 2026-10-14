
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class wArvHtGr {
    public static void main(String[] args) {
        String serverIP = "36.50.135.242";
        int serverPort = 2207;

        String studentCode = "B23DCCN951"; // Thay bằng mã sinh viên
        String questionCode = "wArvHtGr";  // Thay bằng mã câu hỏi thực tế

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            InetAddress address = InetAddress.getByName(serverIP);

            // a. Gửi mã sinh viên và mã câu hỏi
            String message = ";" + studentCode + ";" + questionCode;
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, address, serverPort
            );
            socket.send(sendPacket);

            // b. Nhận dữ liệu từ server
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(
                receiveData, receiveData.length
            );
            socket.receive(receivePacket);

            String response = new String(
                receivePacket.getData(),
                0,
                receivePacket.getLength(),
                StandardCharsets.UTF_8
            ).trim();

            System.out.println("Du lieu nhan: " + response);

            // Tách requestId, n và danh sách số
            String[] parts = response.split(";", 3);

            String requestId = parts[0];
            int n = Integer.parseInt(parts[1]);

            Set<Integer> receivedNumbers = new HashSet<>();

            if (parts.length > 2 && !parts[2].isEmpty()) {
                String[] values = parts[2].split(",");

                for (String value : values) {
                    receivedNumbers.add(Integer.parseInt(value.trim()));
                }
            }

            // c. Tìm các giá trị còn thiếu từ 1 đến n
            List<Integer> missingNumbers = new ArrayList<>();

            for (int i = 1; i <= n; i++) {
                if (!receivedNumbers.contains(i)) {
                    missingNumbers.add(i);
                }
            }

            // Tạo thông điệp trả lời
            StringBuilder result = new StringBuilder(requestId);
            result.append(";");

            for (int i = 0; i < missingNumbers.size(); i++) {
                if (i > 0) {
                    result.append(",");
                }
                result.append(missingNumbers.get(i));
            }

            byte[] resultData = result.toString()
                .getBytes(StandardCharsets.UTF_8);

            // Gửi kết quả về đúng địa chỉ và cổng đã gửi phản hồi
            DatagramPacket resultPacket = new DatagramPacket(
                resultData,
                resultData.length,
                receivePacket.getAddress(),
                receivePacket.getPort()
            );

            socket.send(resultPacket);

            System.out.println("Du lieu gui: " + result);

            // d. Socket tự đóng khi thoát khối try

        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
