package com.team.agility.lscore.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateSerializer extends StdSerializer<Date> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public CustomDateSerializer() {
        this(null);
    }

    public CustomDateSerializer(Class<Date> dateClass) {
        super(dateClass);
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeString(formatter.format(date));
    }
}