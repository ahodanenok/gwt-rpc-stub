package ahodanenok.gwt.stub.core.util;

import com.google.gson.*;
import java.lang.reflect.Field;

public final class JsonUtils {

    private static final Gson GSON;

//    private static final String DEFAULT_DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm:ss";
//    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);
//    private static final List<SimpleDateFormat> DATE_FORMATS = Arrays.asList(
//            DEFAULT_DATE_FORMAT,
//            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private static final JsonParser PARSER;

    static {

        PARSER = new JsonParser();

        // todo: кидать исключение, если незнакомое поле
        GSON = new GsonBuilder()
                .serializeNulls()
//                .setDateFormat(DEFAULT_DATE_FORMAT_PATTERN)
//                .registerTypeAdapter(TimeZone.class, new TimeZoneTypeAdapter().nullSafe())
//                .registerTypeAdapter(Calendar.class, new CalendarTypeAdapter().nullSafe())
//                .registerTypeAdapter(Date.class, new DateTypeAdapter().nullSafe())
                .setExclusionStrategies(new ExclusionStrategy() {

                    public boolean shouldSkipField(FieldAttributes f) {
                        // workaround: когда класс и родитель имеют поле с одинаковым именем
                        return isFieldInSuperclass(f.getDeclaringClass(), f.getName());
                    }

                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }

                    // todo: что-то мне не нравится это решение...
                    private boolean isFieldInSuperclass(Class<?> subclass, String fieldName) {
                        Class<?> superclass = subclass.getSuperclass();
                        Field field;

                        while(superclass != null) {
                            field = getField(superclass, fieldName);
                            if(field != null)
                                return true;
                            superclass = superclass.getSuperclass();
                        }

                        return false;
                    }

                    private Field getField(Class<?> theClass, String fieldName) {
                        try {
                            return theClass.getDeclaredField(fieldName);
                        } catch(Exception e) {
                            return null;
                        }
                    }
                })
                .setPrettyPrinting()
                .create();
    }

    private JsonUtils() { }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> objClass) {
        return GSON.fromJson(json, objClass);
    }
    public static <T> T fromJson(JsonElement json, Class<T> objClass) {
        return GSON.fromJson(json, objClass);
    }
    public static JsonElement toElement(String json) {
        return PARSER.parse(json);
    }


    /*private static class TimeZoneTypeAdapter extends TypeAdapter<TimeZone> {

        private static final Logger LOGGER = Logger.getLogger(TimeZoneTypeAdapter.class.getName());

        @Override
        public void write(JsonWriter out, TimeZone value) throws IOException {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Writing java.util.Timezone with ID: " + value.getID());
            }
            out.value(value.getID());
        }

        @Override
        public TimeZone read(JsonReader in) throws IOException {
            String id = in.nextString();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Reading java.util.Timezone with ID: " + id);
            }
            return TimeZone.getTimeZone(id);
        }
    }

    // todo: refactor - убрать копипасту
    private static class CalendarTypeAdapter extends TypeAdapter<Calendar> {

        private static final Logger LOGGER = Logger.getLogger(TimeZoneTypeAdapter.class.getName());

        @Override
        public void write(JsonWriter out, Calendar value) throws IOException {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Writing java.util.Calendar with time: " + value.getTime());
            }
            out.value(DEFAULT_DATE_FORMAT.format(value.getTime()));
        }

        @Override
        public Calendar read(JsonReader in) throws IOException {
            Date date = null;
            String val = in.nextString();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Reading java.util.Calendar : " + val);
            }

            for (int i = 0; i < DATE_FORMATS.size(); i++) {
                SimpleDateFormat df = DATE_FORMATS.load(i);
                try {
                    date = df.parse(val);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Parsed '" + val + "' with pattern '" + df.toPattern() + "'");
                    }
                    break; // we'd better leave this place
                } catch (ParseException e) {
                    LOGGER.warning(
                            "Couldn't parse date '" + val + "' using pattern '" + df.toPattern() + "'");
                }
            }

            if (date == null) {
                throw new IOException("Can't parse calendar from: " + val);
            }

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c;
        }
    }

    private static class DateTypeAdapter extends TypeAdapter<Date> {

        private static final Logger LOGGER = Logger.getLogger(TimeZoneTypeAdapter.class.getName());

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Writing java.util.Calendar with time: " + value.getTime());
            }
            out.value(DEFAULT_DATE_FORMAT.format(value.getTime()));
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            Date date = null;
            String val = in.nextString();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Reading java.util.Date : " + val);
            }

            for (int i = 0; i < DATE_FORMATS.size(); i++) {
                SimpleDateFormat df = DATE_FORMATS.load(i);
                try {
                    date = df.parse(val);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Parsed '" + val + "' with pattern '" + df.toPattern() + "'");
                    }
                    break; // we'd better leave this place
                } catch (ParseException e) {
                    LOGGER.warning(
                            "Couldn't parse date '" + val + "' using pattern '" + df.toPattern() + "'");
                }
            }

            if (date == null) {
                throw new IOException("Can't parse calendar from: " + val);
            }

            return date;
        }
    }*/
}
