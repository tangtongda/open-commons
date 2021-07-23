open commons

# Add maven dependency

```xml

<dependency>
    <groupId>io.github.tangtongda</groupId>
    <artifactId>open-commons</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```

## ExcelUtil

### Read

**Excel**
![excel](https://img-blog.csdnimg.cn/7472094120ff4127895a8a08aa98fafb.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2Mjg5Mzc3,size_16,color_FFFFFF,t_70)
```java
import com.tangtongda.open.commons.anotations.ExcelColumn;

// Response class
public class Student {

    @ExcelColumn(value = "姓名", col = 0)
    private String name;

    @ExcelColumn(value = "年龄", col = 1)
    private Integer age;

    public static void main(String[] args) {
        // Read Excel File
        List<Student> students = ExcelUtil.read(Student.class, file);
    }
}
```

### Write

```java
import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        // Recommend: Write with Object data list
        List<Student> students = new ArrayLsit<>();
        ExcelUtil.writeExcel(response, students, Student.class, "student");

        // value->row index,value->row data
        Map<String, List<String>> dataList = new HashMap<>();
        // Write with custom headers
        ExcelUtil.writeExcel(response, dataList, headers, "student");

    }
}
```
