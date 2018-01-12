/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata.inserter;

import com.bathwater.dynamodb.helper.IDynamoDBHelper;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.tables.MembershipTableItem;

/**
 *
 * @author rajeshk
 */
public class MembershipInserter {
    
    public static void main(String[] args) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();
        MembershipTableItem plan = new MembershipTableItem();
        
        plan.setName("Basic");
        plan.setDescription("Basic monthly plan with 10 kickoff points");
        plan.setPrice(29.99F);
        plan.setInterval("monthly");
        plan.setKickOffPoints(10);
        plan.setStripePlanID("BWK123");
        
        helper.putItem(plan);
    }
    
}
