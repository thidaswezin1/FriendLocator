package com.thida.friendlocator;

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginUnitTest {
    private static final String FAKE = "Login is successful";

    @Mock
    Context context;

    @Test
    public void isUserAccountValid(){
        LoginActivity activity = new LoginActivity();
        String result = activity.success("hla@gmail.com","1234567");
        assertThat(result,is(FAKE));
    }

}
