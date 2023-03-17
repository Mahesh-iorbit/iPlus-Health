package com.iorbit.iorbithealthapp.Models;

public class RegisterUserResponse {
    private Data data;

    private Statusdet statusdet;

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public Statusdet getStatusdet ()
    {
        return statusdet;
    }

    public void setStatusdet (Statusdet statusdet)
    {
        this.statusdet = statusdet;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", statusdet = "+statusdet+"]";
    }
    public class Statusdet
    {
        private String code;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        private String message;

        private String details;

        public String getCode ()
        {
            return code;
        }

        public void setCode (String code)
        {
            this.code = code;
        }

        public String getDetails ()
        {
            return details;
        }

        public void setDetails (String details)
        {
            this.details = details;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [code = "+code+", details = "+details+"]";
        }
    }
    public class Data
    {
        private String uuid;

        public String getUuid ()
        {
            return uuid;
        }

        public void setUuid (String uuid)
        {
            this.uuid = uuid;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [uuid = "+uuid+"]";
        }
    }
}
