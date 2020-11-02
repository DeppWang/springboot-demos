package wang.depp.consumerapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wang.depp.providerclient.api.UserApi;

/**
 * Created by DEPP WANG on 2/11/2020
 */
@Component
public class ProviderManager {
    @Autowired
    UserApi userApi;


}
