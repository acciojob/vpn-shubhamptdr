package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user = new User();
        if(!countryName.equalsIgnoreCase("IND") || !countryName.equalsIgnoreCase("USA")|| !countryName.equalsIgnoreCase("JPN")|| !countryName.equalsIgnoreCase("AUS")|| !countryName.equalsIgnoreCase("CHI")){
            throw new Exception("Country not found");
        }

            user.setUsername(username);
            user.setPassword(password);

            Country country = new Country();
            //set attr. according to validation
            if(countryName.equalsIgnoreCase("CHI")){
                country.setCountryName(CountryName.CHI);
                country.setCode(CountryName.CHI.toCode());
            }

            if(countryName.equalsIgnoreCase("USA")){
                country.setCountryName(CountryName.USA);
                country.setCode(CountryName.USA.toCode());
            }

            if(countryName.equalsIgnoreCase("IND")){
                country.setCountryName(CountryName.IND);
                country.setCode(CountryName.IND.toCode());
            }
            if(countryName.equalsIgnoreCase("AUA")){
                country.setCountryName(CountryName.AUS);
                country.setCode(CountryName.AUS.toCode());
            }

            if(countryName.equalsIgnoreCase("JPN")){
                country.setCountryName(CountryName.JPN);
                country.setCode(CountryName.JPN.toCode());
            }

            country.setUser(user);
            user.setCountry(country);

            StringBuilder sb = new StringBuilder();
            sb.append(country.getCode()).append(".").append(userRepository3.save(user).getId());
            user.setOriginalIp(sb.toString());

            userRepository3.save(user);


        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        // fetch user & serviceProvider
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        // update user & serviceProvider
        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        //save
        serviceProviderRepository3.save(serviceProvider);
        return user;
    }
}
