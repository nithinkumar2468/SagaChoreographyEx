package org.saga.example.shared.order;

import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import java.time.format.DateTimeFormatterBuilder;

public class DefaultInstantSerializer extends InstantSerializer {
    public DefaultInstantSerializer() {
        super(InstantSerializer.INSTANCE, false, false,
                new DateTimeFormatterBuilder().appendInstant(3).toFormatter());
    }
}
