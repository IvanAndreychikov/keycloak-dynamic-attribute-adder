package com.dynamicattributeadder.keycloak;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.models.GroupModel;


public class DynamicAttributeAdderEventListenerProvider implements EventListenerProvider {
    //private static Logger log = LoggerFactory.getLogger(DynamicAttributeAdderEventListenerProvider.class);

    private final KeycloakSession session;
    private final String realmName;
    private final String groupId;
    private final Map<String, String> customAttributes;

    public DynamicAttributeAdderEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.realmName = System.getenv("KC_DYN_ATTR_ADDER_REALM_NAME");
        this.groupId = System.getenv("KC_DYN_ATTR_ADDER_GROUP_ID");
        String rawCustomAttributes = System.getenv("KC_DYN_ATTR_ADDER_CUSTOM_ATTRIBUTES");
        this.customAttributes = parseDicValues(rawCustomAttributes);
    }

    @Override
    public void onEvent(Event event) {
        //log.warn("onEvent " + event.getId() + " user: " + event.getUserId());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        /*log.warn("onAdminEvent   event.getOperationType(): " + event.getOperationType()
                + " GROUP_MEMBERSHIP: " + event.getResourceTypeAsString());*/
        if (event.getOperationType() == OperationType.CREATE && "GROUP_MEMBERSHIP".equals(event.getResourceTypeAsString())) {
            String[] resourceParts = event.getResourcePath().split("/");
            //log.warn("event.getResourcePath(): " + event.getResourcePath());
            if (resourceParts.length > 1) {
                String userId = resourceParts[1];
                //log.warn("userId: " + resourceParts[1]);
                UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
                //log.warn("user: " + user);

                GroupModel group = session.realms()
                        .getRealmByName(realmName)
                        .getGroupById(groupId);
                //log.warn("group: " + group);
                //log.warn("customAttributes: " + customAttributes);

                if (user != null && group != null && user.isMemberOf(group)) {
                    customAttributes.forEach((key, value) -> {
                        user.setAttribute(key, Collections.singletonList(value));
                    });
                }
            }
        }
    }

    public void close() {
    }

    private static Map<String, String> parseDicValues(String rawString) {
        Map<String, String> result = new HashMap<>();

        String[] pairs = rawString.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                result.put(key, value);
            }
        }
        return result;
    }
}
