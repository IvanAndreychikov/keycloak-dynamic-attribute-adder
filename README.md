# Keycloak dynamic attribute adder plugin


## Overview

This Keycloak plugin is designed for situations where you need to add attributes only to a specific subset of users. When users are added to a designated group, they will receive the specified attributes with the values you defined.



## How to use this plugin

1. You should have proper environment to compile this code. JDK and Maven should be installed on your local machine.
2. Grab this code to your machine and run `mvn clean package` in the root folder.
3. Take compiled *.jar file from 'target' folder and move it to keycloak providers folder, e.g. /opt/keycloak/providers/ 
4. Run your keycloak with following environment variables: 
	+ KC_DYN_ATTR_ADDER_REALM_NAME - to specify the name of target realm (e.g. my-custom-realm)
	+ KC_DYN_ATTR_ADDER_GROUP_ID - to specify the id of the group upon joining which a user will receive additional attributes (e.g. 1bba5907-f860-42b2-b2b9-8d55606dcf08)
	+ KC_DYN_ATTR_ADDER_CUSTOM_ATTRIBUTES - to specify key-value pairs for new attributes which needs to be added, separated by the ';' separator (e.g. field1=value2;field2=value2)
5. Login into admin panel, open your realm and go to 'Realm settings' => 'Events' and select 'dynamic-attribute-adder-listener' in the 'Event listeners' dropdown. Then click 'Save' button.


