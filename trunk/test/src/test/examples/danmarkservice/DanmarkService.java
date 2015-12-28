package test.examples.danmarkservice;

import fadwsclient.FadService;

import java.util.List;

/**
 * Date: Dec 24, 2008
 *
 * @author Christian Hvid
 */

public interface DanmarkService {
    public class Coordinates {
        private String east;
        private String north;

        public Coordinates(String east, String north) {
            this.east = east;
            this.north = north;
        }

        public String getEast() {
            return east;
        }

        public String getNorth() {
            return north;
        }

        public String toString() {
            return east + " east, " + north + " north";
        }
    }

    @FadService(
            requestUrl = "http://oiorest.dk/danmark/adresser/{0},{1},{2}.json",
            responseEncoding = FadService.ResponseEncoding.JSON,
            responseSubhierarchy = "etrs89koor.east,etrs89koor.north"
    )
    public Coordinates findCoordinates(String streetName, String houseNumber, String zipCode);

    @FadService(
            requestUrl = "http://oiorest.dk/danmark/adresser/{0},{1},{2}.json",
            responseEncoding = FadService.ResponseEncoding.JSON,
            responseSubhierarchy = "sogn.navn"
    )
    public String findParish(String streetName, String houseNumber, String zipCode);

    class School {
        private String name;
        private String type;

        public School(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String toString() {
            return name + " (" + type + ")";
        }
    }

    @FadService(
            requestUrl = "http://oiorest.dk/danmark/kommuner/{0}/skoler",
            responseEncoding = FadService.ResponseEncoding.XML,
            responseSubhierarchy = "skoler*.skole.navn,skoler*.skole.type"
    )
    public List<School> listSchoolsByMunicipality(int municipalityId);

    public class Municipality {
        private String id;
        private String name;

        public Municipality(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String toString() {
            return name + " ("+id+")";
        }
    }

    @FadService(
            requestUrl = "http://oiorest.dk/danmark/kommuner?q={0}",
            responseEncoding = FadService.ResponseEncoding.XML,
            responseSubhierarchy = "kommuner*.kommune.nr,kommuner*.kommune.navn"
    )
    public List<Municipality> listMunicipalitiesByName(String name);
}
