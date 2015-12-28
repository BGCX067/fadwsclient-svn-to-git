package test.examples.danmarkservice;

import fadwsclient.FadClient;
import test.examples.danmarkservice.DanmarkService;

/**
 * Date: Dec 26, 2008
 *
 * @author Christian Hvid
 */

public class DanmarkServiceTest {
    public static void main(String[] args) {
        DanmarkService danmarkService = FadClient.create(DanmarkService.class);

        System.out.println("Looking up parish of 'bentzonsvej 11, 2000':");
        System.out.println(danmarkService.findParish("bentzonsvej", "11", "2000"));

        System.out.println("Looking up coordinates of 'bentzonsvej 11, 2000':");
        System.out.println(danmarkService.findCoordinates("bentzonsvej", "11", "2000"));

        System.out.println("Looking up school in municipality '751'");
        for (DanmarkService.School school : danmarkService.listSchoolsByMunicipality(751))
            System.out.println(school.toString());
        
        System.out.println("Finding municipalities matching 'frede':");
        for (DanmarkService.Municipality municipality : danmarkService.listMunicipalitiesByName("frede"))
            System.out.println(municipality.toString());

    }
}
