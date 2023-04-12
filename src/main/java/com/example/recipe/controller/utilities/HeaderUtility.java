package com.example.recipe.controller.utilities;

import org.springframework.http.HttpHeaders;

public class HeaderUtility {


        public static HttpHeaders createAlert(String message, String param) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Recipe-alert", message);
            headers.add("Recipe-params", param);
            return headers;
        }

        public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
            return createAlert("recipe." + entityName + ".created", param);
        }

        public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
            return createAlert("recipe." + entityName + ".updated", param);
        }

        public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
            return createAlert("recipe." + entityName + ".deleted", param);
        }

        public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Recipe-error", "error." + errorKey);
            headers.add("Recipe-params", entityName);
            return headers;
        }


}
