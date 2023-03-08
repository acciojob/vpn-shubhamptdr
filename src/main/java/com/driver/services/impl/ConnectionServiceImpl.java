package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        // fetch user
        User user = userRepository2.findById(userId).get();

        if(user.getConnected()){
            throw new Exception("Already connected");
        }

        if(countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())){
            return user;
        }

        if (user.getServiceProviderList()==null){
            throw new Exception("Unable to connect");
        }

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        int max = Integer.MAX_VALUE;
        ServiceProvider serviceProvider = null;
        Country country =null;

        for(ServiceProvider serviceProvider1:serviceProviderList){
            List<Country> countryList = serviceProvider1.getCountryList();

            for (Country country1: countryList){

                if(countryName.equalsIgnoreCase(country1.getCountryName().toString()) && max > serviceProvider1.getId() ){
                    max=serviceProvider1.getId();
                    serviceProvider=serviceProvider1;
                    country=country1;
                }
            }
        }
        if (serviceProvider!=null){
            Connection connection = new Connection();
            connection.setUser(user);
            connection.setServiceProvider(serviceProvider);

            String countryC = country.getCode();
            int givenId = serviceProvider.getId();
            String mask = countryC+"."+givenId+"."+userId;

            user.setConnected(true);
            user.setMaskedIp(mask);
            user.getConnectionList().add(connection);

            serviceProvider.getConnectionList().add(connection);

            userRepository2.save(user);
            serviceProviderRepository2.save(serviceProvider);

        }

        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        // fetch user
        User user = userRepository2.findById(userId).get();

        // --
        if(user.getConnected()==false){
            throw new Exception("Already disconnected");
        }

        user.setConnected(false);
        user.setMaskedIp(null);
        //save user
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User user = userRepository2.findById(senderId).get();
        User user1 = userRepository2.findById(receiverId).get();

        if(user1.getMaskedIp()!=null){
            String str = user1.getMaskedIp();
            String countryCode = str.substring(0,3);

            if(countryCode.equals(user.getOriginalCountry().getCode())){
                return user;
            }


            String countryName =  CountryName.getCountryName(countryCode);
            User user2 = connect(senderId,countryName);


            if (!user2.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            return user2;

        }

        if(user1.getOriginalCountry().equals(user.getOriginalCountry())){
            return user;
        }

        String countryName = user1.getOriginalCountry().getCountryName().toString();

        User user2 =  connect(senderId,countryName);
        if (!user2.getConnected()){
            throw new Exception("Cannot establish communication");
        }
        return user2;


    }
}
