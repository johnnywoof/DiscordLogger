import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class StringContainsTest {

    @Test
    public void testWarningStr() {

        List<String> words = Collections.singletonList("No client connected");
        String str = "[13:45:31 WARN]: No client connected for pending server!\n";
        String removedCharStr = str.replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("\\[", "").replaceAll("\\]", "");

        boolean case1 = words.stream().anyMatch(str::contains);
        boolean case2 = words.stream().anyMatch(removedCharStr::contains);

        Assert.assertTrue(case1 || case2);

    }

}
