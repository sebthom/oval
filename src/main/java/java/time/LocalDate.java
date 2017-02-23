package java.time;

public abstract class LocalDate {
    public static LocalDate now() {
        throw new UnsupportedOperationException();
    }

    public abstract LocalDateTime atStartOfDay();
}
