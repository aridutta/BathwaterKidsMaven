/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.bathwater.dynamodb.tables.UserRequestTableItem;

/**
 *
 * @author rajeshk
 */
public class UserRequestComparator implements Comparator<UserRequestTableItem> {

    private static final DateFormat FORMATTER = new SimpleDateFormat("MM.dd.yyyy");

    @Override
    public int compare(UserRequestTableItem o1, UserRequestTableItem o2) {
        String dateOne = o1.getDate();
        String dateTwo = o2.getDate();
        try {
            Date date1 = FORMATTER.parse(dateOne);
            Date date2 = FORMATTER.parse(dateTwo);

            int result = date1.compareTo(date2);

            if (result == 0) {
                result = o1.getTime().compareTo(o2.getTime());

                if (result == 0) {
                    if (o1.getUser() != null && o2.getUser() != null) {
                        if (!StringUtil.isBlank(o1.getUser().getZipCode()) && !StringUtil.isBlank(o2.getUser().getZipCode())) {
                            result = o1.getUser().getZipCode().compareTo(o2.getUser().getZipCode());
                        }
                    }
                }

                return -1 * result;
            }

            return -1 * result;
        } catch (ParseException ex) {
            throw new RuntimeException("date format error");
        }
    }

}
