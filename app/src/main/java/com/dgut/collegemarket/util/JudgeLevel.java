package com.dgut.collegemarket.util;

/**
 * Created by Administrator on 2016/12/21.
 */

public class JudgeLevel {

    public static int[] xps={100,300,600,1000,1500,2100,2800,3600,4500};
    /**
     * 判断用户当前等级
     * @param xp
     * @return
     */
    public static int judege(int xp){
        int level =1;
        for(int i=0;i<8;i++){
            if(xp<xps[0]){
                level = 1;
                break;
            }else if(xp>xps[8]){
                level = 10;
                break;
            }else if(xp >=xps[i] && xp<xps[i+1]){
                level = i+2;
                break;
            }
        }
        return level;
    }

    /**
     * 判断当前最大值经验
     * @param xp
     * @return
     */
    public static int juderMax(int xp){
        int max=100;
        for(int i=8;i>= 0;i--){
           if(xp >= xps[i]){
               if(i==8){
                   max = xps[i];
                   break;
               }
                max = xps[i+1];
                break;
            }else{
                max = xps[i];
            }
        }
        return max;
    }
}
