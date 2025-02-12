package ticket.booking.util;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil{

    public static String hashedPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword,BCrypt.gensalt());
    }

    public static Boolean checkPassword(String hashedPassword , String plainPassword) {
        return BCrypt.checkpw(plainPassword,hashedPassword);
    }


}