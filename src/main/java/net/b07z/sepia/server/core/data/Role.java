package net.b07z.sepia.server.core.data;

/**
 * User roles for account. Certain activities require certain roles.
 * 
 * @author Daniel, Florian
 *
 */
public enum Role {
	unknown,
	developer, seniordev, chiefdev,
	tester, inviter,
	translator,
	user, superuser,
	assistant,
	thing, 				//e.g. for IoT devices
	smarthomeguest		//for smart home skills that run on-device
}