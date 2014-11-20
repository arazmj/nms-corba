package ex.corba.ciena.error;

public enum CorbaErrorDescriptions {

	EXCPT_NOT_IMPLEMENTED(
			0,
			"EXCPT_NOT_IMPLEMENTED",
			PRIORITY.MINOR,
			"If some IDL operations are optional or not implemented in this release, then this enum may be used for this purpose.  If the operation itself is not supported, then errorReason shall be an empty string. If this exception is raised because of the values of specific parameters, then the names of these parameters shall be supplied in errorReason (separated by commas), unless otherwise specified in the operation description."),

	EXCPT_INTERNAL_ERROR(1, "EXCPT_INTERNAL_ERROR", PRIORITY.MINOR,
			"To indicate an EMS internal error. Applies to all methods."),

	EXCPT_INVALID_INPUT(
			2,
			"EXCPT_INVALID_INPUT",
			PRIORITY.MINOR,
			"If the format of a parameter is incorrect, e.g. if a TP name which is a 3 level namingAttribute is passed as a single level name, then this type will be used.  Also if a parameter is out of range, this type will be used. The reason field will be filled with the parameter that was incorrect"),

	EXCPT_OBJECT_IN_USE(3, "EXCPT_OBJECT_IN_USE", PRIORITY.MINOR,
			"To indicate an object already in use."),

	EXCPT_TP_INVALID_ENDPOINT(
			4,
			"EXCPT_TP_INVALID_ENDPOINT",
			PRIORITY.MINOR,
			"To indicate that the specified TP does not exist or cannot be created (e.g., attempt to create a VPL TP using an out of range VPI value). Note that if the TP is valid but is already terminated & mapped or cross-connected then EXCPT_OBJECT_IN_USE must be returned."),

	EXCPT_ENTITY_NOT_FOUND(
			5,
			"EXCPT_ENTITY_NOT_FOUND",
			PRIORITY.MINOR,
			"In general, if the NMS supplies an object name as a parameter to an operation and the EMS can not find the object with the given name then an exception of this type is returned. The reason field in the exception will be filled with the name that was passed in as parameter."),

	EXCPT_TIMESLOT_IN_USE(6, "EXCPT_TIMESLOT_IN_USE", PRIORITY.MINOR,
			"To indicate a timeslot already in use when creating or activating an SNC."),

	EXCPT_PROTECTION_EFFORT_NOT_MET(
			7,
			"EXCPT_PROTECTION_EFFORT_NOT_MET",
			PRIORITY.MINOR,
			"If the NMS requests an SNC with a protection effort that cannot be met by the EMS."),

	EXCPT_NOT_IN_VALID_STATE(8, "EXCPT_NOT_IN_VALID_STATE", PRIORITY.MINOR,
			"Used if the client tries to delete an active SNC for example."),

	EXCPT_UNABLE_TO_COMPLY(
			9,
			"EXCPT_UNABLE_TO_COMPLY",
			PRIORITY.MAJOR,
			"The value EXCPT_UNABLE_TO_COMPLY value is used as a generic value when a server cannot respond to the request."),

	EXCPT_NE_COMM_LOSS(
			10,
			"EXCPT_NE_COMM_LOSS",
			PRIORITY.MAJOR,
			"The value EXCPT_NE_COMM_LOSS value is used as a generic value when a server cannot communicate with the NE and that prevents the successful completion of the operation.  All operations that involve communication with the NE may throw this particular exception type."),

	EXCPT_CAPACITY_EXCEEDED(
			11,
			"EXCPT_CAPACITY_EXCEEDED",
			PRIORITY.MINOR,
			"Raised when an operation will result in resources being created or activated beyond the capacity supported by the NE/EMS."),

	EXCPT_ACCESS_DENIED(12, "EXCPT_ACCESS_DENIED", PRIORITY.MAJOR,
			"Raised when an operation results in a security violation."),

	EXCPT_TOO_MANY_OPEN_ITERATORS(
			13,
			"EXCPT_TOO_MANY_OPEN_ITERATORS",
			PRIORITY.MAJOR,
			"Raised when an EMS exceeds its internal limit of the number of iterators it can support."),

	EXCPT_UNSUPPORTED_ROUTING_CONSTRAINTS(
			14,
			"EXCPT_UNSUPPORTED_ROUTING_CONSTRAINTS",
			PRIORITY.MINOR,
			"Raised when an EMS does not support the routing constraints specified as input."),

	EXCPT_USERLABEL_IN_USE(15, "EXCPT_USERLABEL_IN_USE", PRIORITY.MAJOR,
			"Raised when the userLabel uniqueness constraint  can not be met.");

	public static enum PRIORITY {
		MAJOR, MINOR
	}

	private PRIORITY priority = PRIORITY.MAJOR;
	private final int errorCode;
	private final String errorType;
	private final String errorDescription;

	private CorbaErrorDescriptions(int errorCode, String errorType,
			PRIORITY priority, String errorDescription) {
		this.errorCode = errorCode;
		this.errorType = errorType;
		this.priority = priority;
		this.errorDescription = errorDescription;
	}

	public String getErrorString() {
		return "ErrorCode: " + errorCode + " ErrorType: " + errorType
				+ " ErrorDescription: " + errorDescription;
	}

	public PRIORITY getPriority() {
		return priority;
	}
}
