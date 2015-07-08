package ua.opensvit.api;

import java.util.Vector;

public class LevtvStruct {
    public IptvFilmStruct Film_struct = new IptvFilmStruct();
    public IptvChStruct Iptv_channels = new IptvChStruct();
    public IptvEpgStruct Iptv_epg = new IptvEpgStruct();
    public IptvOsdStruct Osd_struct = new IptvOsdStruct();

    public LevtvStruct() {
    }

    public class IptvChStruct {
        public items IptvChanelsItems = new items();
        public boolean success;
        public int total;

        public IptvChStruct() {
        }

        public class items {
            public Vector<Boolean> allowed = new Vector(0);
            public Vector<Boolean> archive = new Vector(0);
            public Vector<Boolean> favorite = new Vector(0);
            public Vector<Integer> id = new Vector(0);
            public Vector<String> logo = new Vector(0);
            public Vector<String> name = new Vector(0);

            public items() {
            }
        }
    }

    public class IptvEpgStruct {
        public items IptvEPGItems = new items();
        public int day;
        public int dayOfWeek;
        public String description;
        public boolean success;
        public int total;

        public IptvEpgStruct() {
        }

        public class items {
            public items() {
            }
        }
    }

    public class IptvFilmStruct {
        public String actor;
        public String country;
        public String description;
        public int duration;
        public String genre;
        public int id;
        public String logo;
        public String name;
        public String origin;
        public int price;
        public boolean success;
        public int year;

        public IptvFilmStruct() {
        }
    }

    public class IptvOsdStruct {
        public Programs IptvOsdItems = new Programs();
        public boolean success;

        public IptvOsdStruct() {
        }

        public class Programs {
            public Vector<Integer> currTime = new Vector(0);
            public Vector<Integer> duration = new Vector(0);
            public Vector<Integer> durationTime = new Vector(0);
            public Vector<String> end = new Vector(0);
            public Vector<String> start = new Vector(0);
            public Vector<String> title = new Vector(0);

            public Programs() {
            }
        }
    }
}
