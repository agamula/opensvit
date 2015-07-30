package ua.opensvit.data.constants;

public class ApiConstants {
    private ApiConstants() {
    }

    private static final String WS = "/ws/";

    public static final class LoginPasswordAuth {
        private LoginPasswordAuth() {
        }

        public static final String AUTH_URL = WS + "Auth?login=%s&password=%s";
        public static final class Auth {
            private Auth() {
            }

            public static final String URL = WS + "Auth";
            public static final String PARAM_LOGIN = "login";
            public static final String PARAM_PASSWORD = "password";
        }

        public static final String IP_TV_MENU_URL = WS + "IptvMenu";
        public static final String VOD_MENU_URL = WS + "VodMenu";
        public static final String GET_CHANNELS_URL = WS +
                "GetChannels?genreId=%s&perPage=%s&page=%s";
        public static final String TOGGLE_IP_TV_FAVORITES_URL = WS +
                "ToggleIptvFavorites?iptvId=%s";

        public static final String CHECK_AVAILABILITY_URL = WS + "CheckAvailability?url=%s";
        public static final class CheckAvailability {
            private CheckAvailability() {
            }

            public static final String URL = WS + "CheckAvailability";
            public static final String PARAM_URL = "url";
        }

        public static final String CHECK_SERVICE_PIN_URL = WS + "CheckServicePin?id=%s&pin=%s";
        public static final class CheckServicePin {
            private CheckServicePin() {
            }

            public static final String URL = WS + "CheckServicePin";
            public static final String PARAM_ID = "id";
            public static final String PARAM_PIN = "pin";
        }

        public static final String GET_ARCHIVE_URL = WS + "GetArchiveUrl?id=%s&timestamp=%s";
        public static final String GET_CHANNEL_IP_URL = WS + "GetChannelIp?id=%s";
        public static final String GET_CHANNEL_OSD_URL = WS +
                "GetChannelOsd?channelId=%s&serviceId=%s&timestamp=%s";

        public static final String GET_CREEPING_LINE_URL = WS +
                "GetCreepingLine?service=%s&looking=%s";
        public static final String GET_EPG_URL = WS +
                "GetEpg?channelId=%s&serviceId=%s&startUT=%s&endUT=%s&perPage=%s&page=%s";
        public static final String GET_FILM_URL = WS + "GetFilm?id=%s";
        public static final String GET_FILMS_URL = WS + "GetFilms?genreId=%s&perPage=%s&page=%s";
        public static final String GET_IMAGES_URL = WS + "GetImages";
        public static final String I18N_URL = WS + "I18n?language=%s";
        public static final String INFO_ABOUT_URL = WS + "InfoAbout";
        public static final String KEEP_ALIVE_URL = WS + "KeepAlive";
        public static final String ORDER_FILM_URL = WS + "OrderFilm?id=%s&pin=%s";
        public static final String RESET_PIN_URL = WS + "ResetPin?pin=%s&oldPin=%s";
        public static final class ResetPin {
            private ResetPin() {
            }

            public static final String URL = WS + "ResetPin";
            public static final String PARAM_PIN = "pin";
            public static final String PARAM_OLD_PIN = "oldPin";
        }

        public static final String UPDATE_PROFILE_URL = WS +
                "UpdateProfile?id=%s&type=%s&language=%s&ratio=%s&resolution=%s&skin=%s" +
                "&transparency=%s&startPage=%s&networkPath=%s&volume=%s&reminder=%s";
    }

    public static final class IpTvMenu {
        private IpTvMenu() {
        }

        public static final String URL = WS + "IptvMenu";
    }

    public static final class VodMenu {
        private VodMenu() {
        }

        public static final String URL = WS + "VodMenu";
    }

    public static final class GetChannels {
        private GetChannels() {
        }

        public static final String URL = WS + "GetChannels";
        public static final String PARAM_GENRE_ID = "genreId";
        public static final String PARAM_PER_PAGE = "perPage";
        public static final String PARAM_PAGE = "page";
    }

    public static final class ToggleIpTvFavorites {
        private ToggleIpTvFavorites() {
        }

        public static final String URL = WS + "ToggleIptvFavorites";
        public static final String PARAM_IP_TV = "iptvId";
    }

    public static final class GetArchiveUrl {
        private GetArchiveUrl() {
        }

        public static final String URL = WS + "GetArchiveUrl";
        public static final String PARAM_ID = "id";
        public static final String PARAM_TIMESTAMP = "timestamp";
    }

    public static final class GetChannelIp {
        private GetChannelIp() {
        }

        public static final String URL = WS + "GetChannelIp";
        public static final String PARAM_ID = "id";
    }

    public static final class GetChannelOsd {
        private GetChannelOsd() {
        }

        public static final String URL = WS + "GetChannelOsd";
        public static final String PARAM_CHANNEL_ID = "channelId";
        public static final String PARAM_SERVICE_ID = "serviceId";
        public static final String PARAM_TIMESTAMP = "timestamp";
    }

    public static final class GetCreepingLine {
        private GetCreepingLine() {
        }

        public static final String URL = WS + "GetCreepingLine";
        public static final String PARAM_SERVICE = "service";
        public static final String PARAM_LOOKING = "looking";
    }

    public static final class GetEpg {
        private GetEpg() {
        }

        public static final String URL = WS + "GetEpg";
        public static final String PARAM_CHANNEL_ID = "channelId";
        public static final String PARAM_SERVICE_ID = "serviceId";
        public static final String PARAM_START_UT = "startUT";
        public static final String PARAM_END_UT = "endUT";
        public static final String PARAM_PER_PAGE = "perPage";
        public static final String PARAM_PAGE = "page";
    }

    public static final class GetFilm {
        private GetFilm() {
        }

        public static final String URL = WS + "GetFilm";
        public static final String PARAM_ID = "id";
    }

    public static final class GetFilms {
        private GetFilms() {
        }

        public static final String URL = WS + "GetFilms";
        public static final String PARAM_GENRE_ID = "genreId";
        public static final String PARAM_PER_PAGE = "perPage";
        public static final String PARAM_PAGE = "page";
    }

    public static final class GetImages {
        private GetImages() {
        }

        public static final String URL = WS + "GetImages";
    }

    public static final class I18n {
        private I18n() {
        }

        public static final String URL = WS + "I18n";
    }

    public static final class InfoAbout {
        private InfoAbout() {
        }

        public static final String URL = WS + "InfoAbout";
    }

    public static final class KeepAlive {
        private KeepAlive() {
        }

        public static final String URL = WS + "KeepAlive";
    }

    public static final class OrderFilm {
        private OrderFilm() {
        }

        public static final String URL = WS + "OrderFilm";
        public static final String PARAM_ID = "id";
        public static final String PARAM_PIN = "pin";
    }

    public static final class UpdateProfile {
        private UpdateProfile() {
        }

        public static final String URL = WS + "UpdateProfile";
        public static final String PARAM_ID = "id";
        public static final String PARAM_TYPE = "type";
        public static final String PARAM_LANGUAGE = "language";
        public static final String PARAM_RATIO = "ratio";
        public static final String PARAM_RESOLUTION = "resolution";
        public static final String PARAM_SKIN = "skin";
        public static final String PARAM_TRANSPARENCY = "transparency";
        public static final String PARAM_START_PAGE = "startPage";
        public static final String PARAM_NETWORK_PATH = "networkPath";
        public static final String PARAM_VOLUME = "volume";
        public static final String PARAM_REMINDER = "reminder";
    }


    public static final class MacAddressAuth {
        private MacAddressAuth() {
        }

        public static final String AUTH_URL = WS + "AuthStb?mac=%s&sn=%s";
        public static final String AUTH_LOGIN_PASSWORD_URL = WS +
                "AuthStb?mac=%s&sn=%s&login=%s&password=%s";
        public static final class Auth {
            private Auth() {
            }

            public static final String URL = WS + "AuthStb";
            public static final String PARAM_MAC = "mac";
            public static final String PARAM_SN = "sn";

            public static final class LoginPassword {
                private LoginPassword() {
                }

                public static final String PARAM_LOGIN = "login";
                public static final String PARAM_PASSWORD = "password";
            }
        }

        public static final String GET_ARCHIVE_URL = WS + "GetArchiveUrl?id=%s&timestamp=%s";
        public static final String GET_CHANNEL_IP_URL = WS + "GetChannelIp?id=%s";
        public static final String GET_CHANNEL_OSD_URL = WS +
                "GetChannelOsd?channelId=%s&serviceId=%s&timestamp=%s";
        public static final String GET_CHANNELS_URL = WS +
                "GetChannels?genreId=%s&perPage=%s&page=%s";
        public static final String GET_CREEPING_LINE_URL = WS +
                "GetCreepingLine?service=%s&looking=%s";
        public static final String GET_EPG_URL = WS +
                "GetEpg?channelId=%s&serviceId=%s&startUT=%s&endUT=%s&perPage=%s&page=%s";
        public static final String GET_FILM_URL = WS + "GetFilm?id=%s";
        public static final String GET_FILMS_URL = WS + "GetFilms?genreId=%s&perPage=%s&page=%s";
        public static final String GET_IMAGES_URL = WS + "GetImages";
        public static final String I18N_URL = WS + "I18n?language=%s";
        public static final String INFO_ABOUT_URL = WS + "InfoAbout";
        public static final String IP_TV_MENU_URL = WS + "IptvMenu";
        public static final String KEEP_ALIVE_URL = WS + "KeepAlive";
        public static final String ORDER_FILM_URL = WS + "OrderFilm?id=%s&pin=%s";
        public static final String TOGGLE_IP_TV_FAVORITES_UTL = WS +
                "ToggleIptvFavorites?iptvId=%s";
        public static final String VOD_MENU_URL = WS + "VodMenu";
        public static final String UPDATE_PROFILE_URL = WS +
                "UpdateProfile?id=%s&type=%s&language=%s&ratio=%s&resolution=%s&skin=%s" +
                "&transparency=%s&startPage=%s&networkPath=%s&volume=%s&reminder=%s";

    }
}
