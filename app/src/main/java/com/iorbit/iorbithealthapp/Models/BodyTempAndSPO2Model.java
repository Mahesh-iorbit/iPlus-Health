package com.iorbit.iorbithealthapp.Models;

public class BodyTempAndSPO2Model {
    public String value;
    public String value2;
    String date;
    String type;
    int img;

    public BodyTempAndSPO2Model(String value, String date, String type, int img, String value2) {
        this.value = value;
        this.date = date;
        this.type = type;
        this.img = img;
        this.value2 = value2;
    }

    public String getValue() {
        if(value2.equals("") || type.equals("BG"))
            return value;
        return value+"/"+value2;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {

        if(!type.equalsIgnoreCase("FALL")) {
            float v = Float.parseFloat(value);
            if (type.equalsIgnoreCase("BT")) {
                if (v <= 95) {
                    return "Hypothermia";
                } else if (v > 95 && v <= 99) {
                    return "Normal";
                } else if (v > 99 && v <= 104) {
                    return "Fever";
                } else if (v > 104) {
                    return "Hyperpyrexia";
                }
            } else if (type.equals("SPO2")) {
                if (v < 75) {
                    return "Hypoxemia";
                } else if (v >= 75 && v <= 89) {
                    return "Hypoxemia";
                } else if (v >= 90 && v <= 94) {
                    return "Hypoxemia";
                } else if (v >= 95) {
                    return "Normal";
                }
            } else if (type.equals("BP")) {
                int v2 = Integer.parseInt(value2);

                if (v == 0 || v2 == 0)
                    return "Error_Record";
                else if (v < 120 && v2 <= 80)
                    return "Normal";
                else if (v >= 120 && v <= 129 && v2 <= 80)
                    return "Elevated";
                else if ((v >= 130 && v <= 139) || (v2 >= 80 && v2 <= 89))
                    return "Hypertension State-I";
                else if ((v >= 140 && v < 180) || (v2 >= 90 && v2 < 120))
                    return "Hypertension State-II";
                else if (v >= 180 || v2 >= 120)
                    return "Severe Hypertension";
                else
                    return "Elevated";

            } else if (type.equals("BG")) {
                if (value2.equals("19"))
                    return "Random Blood Sugar";
                else if (value2.equals("18"))
                    return "Postprandial";
                else if (value2.equals("17"))
                    return "Fasting Blood Sugar";

            } else if (type.equals("BPM")) {
                if (v < 95)
                    return "Low";
                else if (v >= 95 && v <= 114)
                    return "Very light";
                else if (v > 114 && v <= 133)
                    return "Light";
                else if (v > 133 && v <= 152)
                    return "Moderate";
                else if (v > 152 && v <= 171)
                    return "Hard";
                else if (v > 171 && v <= 190)
                    return "Very Hard";

            } else if (type.equals("FALL")) {
                return "";
            }
        }
        else
            return "zgvxfg-dfg-hhdh";
        return "NoneError";
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
