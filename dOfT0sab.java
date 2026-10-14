
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class dOfT0sab {
    public static void main(String[] args) {
        String serverIP = "36.50.135.242";
        int serverPort = 2208;
        String studentCode = "B23DCCN951"; // Thay mã sinh viên
        String questionCode = "dOfT0sab";  // Thay mã câu hỏi

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);
            InetAddress address = InetAddress.getByName(serverIP);

            String message = ";" + studentCode + ";" + questionCode;
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(sendData, sendData.length, address, serverPort));

            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
            int separator = response.indexOf(';');
            String requestId = response.substring(0, separator);
            String data = response.substring(separator + 1);
            if (data.startsWith(" ")) data = data.substring(1);

            Map<Character, Integer> frequency = new LinkedHashMap<>();
            for (char c : data.toCharArray()) frequency.put(c, frequency.getOrDefault(c, 0) + 1);

            int maxFrequency = 0;
            for (int count : frequency.values()) maxFrequency = Math.max(maxFrequency, count);

            char mostFrequent = '\0';
            for (char c : data.toCharArray()) {
                if (frequency.get(c) == maxFrequency) {
                    mostFrequent = c;
                    break;
                }
            }

            StringBuilder positions = new StringBuilder();
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == mostFrequent) positions.append(i + 1).append(",");
            }

            String result = requestId + ";" + mostFrequent + ":" + positions;
            byte[] resultData = result.getBytes(StandardCharsets.UTF_8);
            DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
            socket.send(resultPacket);

            System.out.println(result);
        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
