package manifold.graphql.sample.scalars;


import manifold.json.rt.api.IJsonFormatTypeCoercer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import static manifold.ext.rt.api.ICallHandler.UNHANDLED;

/**
 * Implement {@code "URL"} format. Maps to corresponding Java class {@link URL}. Note this is a non-standard JSON Schema
 * format, but it may be useful where formatted URLs exist in a schema.
 */
public class UrlCoercer implements IJsonFormatTypeCoercer {
    @Override
    public Map<String, Class<?>> getFormats() {
        return Collections.singletonMap("URL", URL.class);
    }

    @Override
    public Object coerce(Object value, Class<?> type) {
        //
        // From JSON value to Java value
        //
        if (type == URL.class && value instanceof String) {
            try {
                return new URL((String) value);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        //
        // From Java value to JSON value
        //
        if (value instanceof URL && type == String.class) {
            return value.toString();
        }

        return UNHANDLED;
    }

    @Override
    public Object toBindingValue(Object value) {
        if (value instanceof URL) {
            return value.toString();
        }

        return UNHANDLED;
    }
}
