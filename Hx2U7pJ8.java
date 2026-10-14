
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Hx2U7pJ8 {
    public static void main(String[] args) {
        String serverIP = "36.50.135.242";
        int serverPort = 2208;
        String studentCode = "B23DCCN951"; 
        String questionCode = "Hx2U7pJ8";  

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);
            InetAddress address = InetAddress.getByName(serverIP);

            String message = ";" + studentCode + ";" + questionCode;
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(sendData, sendData.length, address, serverPort));

            byte[] receiveData = new byte[8192];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
            int separator = response.indexOf(';');
            if (separator < 0) throw new IllegalArgumentException("Sai dinh dang phan hoi");

            String requestId = response.substring(0, separator);
            String data = response.substring(separator + 1);

            StringBuilder normalized = new StringBuilder();
            boolean newWord = true;

            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);

                if (Character.isLetter(c)) {
                    normalized.append(newWord ? Character.toUpperCase(c) : Character.toLowerCase(c));
                    newWord = false;
                } else {
                    normalized.append(c);
                    newWord = Character.isWhitespace(c);
                }
            }

            String result = requestId + ";" + normalized;
            byte[] resultData = result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket = new DatagramPacket(
                resultData, resultData.length,
                receivePacket.getAddress(), receivePacket.getPort()
            );
            socket.send(resultPacket);

            System.out.println(result);
        } catch (SocketTimeoutException e) {
            System.out.println("Het thoi gian cho phan hoi tu server.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
