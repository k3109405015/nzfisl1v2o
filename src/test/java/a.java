import com.kun.Student;
import com.kun.User;
import com.kun.tools.BeanUtil;

public class a {

    public static void main(String[] args) {
        User user = new User("33");

        Student copy = BeanUtil.copy(user, Student.class);

        System.out.println(copy);
    }

}
