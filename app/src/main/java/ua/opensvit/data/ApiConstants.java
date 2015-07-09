package ua.opensvit.data;

public class ApiConstants {
    private static final String WS = "/ws/";

    public static final class LoginPasswordAuth {
        private LoginPasswordAuth() {
        }

        public static final String AUTH_URL = WS + "Auth?login=%s&password=%s";
        public static final String AUTH_SERVICE_URL = WS + "AuthService?type=%s";
        public static final String AUTH_SERVICE_IP_TV = "iptv";

        public static final String CHECK_AVAILABILITY_URL = WS + "CheckAvailability?url=%s";
        public static final String CHECK_SERVICE_PIN_URL = WS + "CheckServicePin?id=%s&pin=%s";
        public static final String GET_ARCHIVE_URL = WS + "GetArchiveUrl?id=%s&timestamp=%s";
        public static final String GET_CHANNEL_IP_URL = WS + "GetChannelIp?id=%s";
        public static final String GET_CHANNEL_OSD_URL = WS +
                "GetChannelOsd?channelId=%s&serviceId=%s&timestamp=%s";

        public static final String GET_CHANNELS_URL = WS + "GetChannels?genreId=%s&perPage=%s&page=%s";
        public static final String GET_CREEPING_LINE_URL = WS + "GetCreepingLine?service=%s&looking=%s";
        public static final String GET_EPG_URL = WS +
                "GetEpg?channelId=%s&serviceId=%s&startUT=%s&endUT=%s&perPage=%s&page=%s";
        public static final String GET_FILM_URL = WS + "GetFilm?id=%s";
        public static final String GET_FILMS_URL = WS + "GetFilms?genreId=%s&perPage=%s&page=%s";
        public static final String GET_IMAGES_URL = WS + "GetImages";
        public static final String I18N_URL = WS + "I18n?language=%s";
        public static final String INFO_ABOUT_URL = WS + "InfoAbout";
        public static final String IP_TV_MENU_ABOUT_URL = WS + "IptvMenu";
        public static final String KEEP_ALIVE_URL = WS + "KeepAlive";
        public static final String ORDER_FILM_URL = WS + "OrderFilm?id=%s&pin=%s";
        public static final String RESET_PIN_URL = WS + "ResetPin?pin=%s&oldPin=%s";
        public static final String TOGGLE_IP_TV_FAVORITES_UTL = WS + "ToggleIptvFavorites?iptvId=%s";
        public static final String VOD_MENU_URL = WS + "VodMenu";
        public static final String UPDATE_PROFILE_URL = WS +
                "UpdateProfile?id=%s&type=%s&language=%s&ratio=%s&resolution=%s&skin=%s&transparency" +
                "=%s&startPage=%s&networkPath=%s&volume=%s&reminder=%s";
    }

    public static final class MacAddressAuth {
        private MacAddressAuth() {
        }

        public static final String AUTH_URL = WS + "AuthStb?mac=%s&sn=%s";
    }
}
