import UDP.Customer;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class bo9xWhYP {
    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN951";
        String qCode = "bo9xWhYP";
        String serverIP = "36.50.135.242";

        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(serverIP);

        // 1. Gui ma sinh vien va ma cau hoi
        String message = ";" + studentCode + ";" + qCode;
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

        DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, address, 2209
        );
        socket.send(sendPacket);

        // 2. Nhan du lieu tu server
        byte[] buffer = new byte[65507];
        DatagramPacket receivePacket =
                new DatagramPacket(buffer, buffer.length);

        socket.receive(receivePacket);

        byte[] data = receivePacket.getData();
        int length = receivePacket.getLength();

        // 8 byte dau la requestId
        String requestId = new String(
                data, 0, 8, StandardCharsets.UTF_8
        );

        // Cac byte con lai la doi tuong Customer
        ByteArrayInputStream bais =
                new ByteArrayInputStream(data, 8, length - 8);
        ObjectInputStream ois = new ObjectInputStream(bais);

        Customer customer = (Customer) ois.readObject();

        // 3. Chuan hoa ten
        // nguyen van hai duong -> DUONG, Nguyen Van Hai
        String[] words = customer.getName()
                .trim()
                .toLowerCase(Locale.ROOT)
                .split("\\s+");

        StringBuilder normalized = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            if (i > 0) {
                normalized.append(" ");
            }

            normalized.append(
                    words[i].substring(0, 1).toUpperCase(Locale.ROOT)
            ).append(words[i].substring(1));
        }

        String lastName = words[words.length - 1]
                .toUpperCase(Locale.ROOT);

        String newName = lastName + ", " + normalized;
        customer.setName(newName);

        // 4. Chuyen ngay sinh mm-dd-yyyy -> dd/MM/yyyy
        SimpleDateFormat inputFormat =
                new SimpleDateFormat("MM-dd-yyyy");
        inputFormat.setLenient(false);

        SimpleDateFormat outputFormat =
                new SimpleDateFormat("dd/MM/yyyy");

        Date date = inputFormat.parse(customer.getDayOfBirth());

        customer.setDayOfBirth(outputFormat.format(date));

        // 5. Tao username tu ho ten
        // Nguyen Van Hai Duong -> nvhduong
        StringBuilder userName = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            userName.append(words[i].charAt(0));
        }

        userName.append(words[words.length - 1]);

        customer.setUserName(userName.toString());

        // 6. Chuyen doi tuong Customer thanh byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(customer);
        oos.flush();

        byte[] objectData = baos.toByteArray();

        // 7. Ghep requestId (8 byte) voi doi tuong
        byte[] result = new byte[8 + objectData.length];

        System.arraycopy(
                requestId.getBytes(StandardCharsets.UTF_8),
                0, result, 0, 8
        );

        System.arraycopy(
                objectData, 0, result, 8, objectData.length
        );

        // 8. Gui lai server
        DatagramPacket resultPacket = new DatagramPacket(
                result, result.length, address, 2209
        );

        socket.send(resultPacket);

        // 9. Dong tai nguyen
        ois.close();
        oos.close();
        socket.close();
    }
}