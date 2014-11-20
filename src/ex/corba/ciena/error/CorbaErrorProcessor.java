package ex.corba.ciena.error;

import com.netcracker.ciena.oncenter.v11.globaldefs.ProcessingFailureException;

public class CorbaErrorProcessor {
	private ProcessingFailureException prf;

	public CorbaErrorProcessor(ProcessingFailureException prf) {
		this.prf = prf;
	}

	public static synchronized String printError(ProcessingFailureException _prf) {
		int errorCode = _prf.exceptionType.value();
		String str_error = "ProcessingFailureException: reason: "
				+ _prf.errorReason + ", ";
		switch (errorCode) {
		case 0:
			str_error += CorbaErrorDescriptions.EXCPT_NOT_IMPLEMENTED
					.getErrorString();
			break;
		case 1:
			str_error += CorbaErrorDescriptions.EXCPT_INTERNAL_ERROR
					.getErrorString();
			break;
		case 2:
			str_error += CorbaErrorDescriptions.EXCPT_INVALID_INPUT
					.getErrorString();
			break;
		case 3:
			str_error += CorbaErrorDescriptions.EXCPT_OBJECT_IN_USE
					.getErrorString();
			break;
		case 4:
			str_error += CorbaErrorDescriptions.EXCPT_TP_INVALID_ENDPOINT
					.getErrorString();
			break;
		case 5:
			str_error += CorbaErrorDescriptions.EXCPT_ENTITY_NOT_FOUND
					.getErrorString();
			break;
		case 6:
			str_error += CorbaErrorDescriptions.EXCPT_TIMESLOT_IN_USE
					.getErrorString();
			break;
		case 7:
			str_error += CorbaErrorDescriptions.EXCPT_PROTECTION_EFFORT_NOT_MET
					.getErrorString();
			break;
		case 8:
			str_error += CorbaErrorDescriptions.EXCPT_NOT_IN_VALID_STATE
					.getErrorString();
			break;
		case 9:
			str_error += CorbaErrorDescriptions.EXCPT_UNABLE_TO_COMPLY
					.getErrorString();
			break;
		case 10:
			str_error += CorbaErrorDescriptions.EXCPT_NE_COMM_LOSS
					.getErrorString();
			break;
		case 11:
			str_error += CorbaErrorDescriptions.EXCPT_CAPACITY_EXCEEDED
					.getErrorString();
			break;
		case 12:
			str_error += CorbaErrorDescriptions.EXCPT_ACCESS_DENIED
					.getErrorString();
			break;
		case 13:
			str_error += CorbaErrorDescriptions.EXCPT_TOO_MANY_OPEN_ITERATORS
					.getErrorString();
			break;
		case 14:
			str_error += CorbaErrorDescriptions.EXCPT_UNSUPPORTED_ROUTING_CONSTRAINTS
					.getErrorString();
			break;
		case 15:
			str_error += CorbaErrorDescriptions.EXCPT_USERLABEL_IN_USE
					.getErrorString();
			break;
		default:
			str_error += "";
			break;
		}
		return str_error;
	}

	public static synchronized CorbaErrorDescriptions.PRIORITY getPriority(
			ProcessingFailureException _prf) {
		int errorCode = _prf.exceptionType.value();
		CorbaErrorDescriptions.PRIORITY int_pr = CorbaErrorDescriptions.PRIORITY.MAJOR;
		switch (errorCode) {
		case 0:
			int_pr = CorbaErrorDescriptions.EXCPT_NOT_IMPLEMENTED.getPriority();
			break;
		case 1:
			int_pr = CorbaErrorDescriptions.EXCPT_INTERNAL_ERROR.getPriority();
			break;
		case 2:
			int_pr = CorbaErrorDescriptions.EXCPT_INVALID_INPUT.getPriority();
			break;
		case 3:
			int_pr = CorbaErrorDescriptions.EXCPT_OBJECT_IN_USE.getPriority();
			break;
		case 4:
			int_pr = CorbaErrorDescriptions.EXCPT_TP_INVALID_ENDPOINT
					.getPriority();
			break;
		case 5:
			int_pr = CorbaErrorDescriptions.EXCPT_ENTITY_NOT_FOUND
					.getPriority();
			break;
		case 6:
			int_pr = CorbaErrorDescriptions.EXCPT_TIMESLOT_IN_USE.getPriority();
			break;
		case 7:
			int_pr = CorbaErrorDescriptions.EXCPT_PROTECTION_EFFORT_NOT_MET
					.getPriority();
			break;
		case 8:
			int_pr = CorbaErrorDescriptions.EXCPT_NOT_IN_VALID_STATE
					.getPriority();
			break;
		case 9:
			int_pr = CorbaErrorDescriptions.EXCPT_UNABLE_TO_COMPLY
					.getPriority();
			break;
		case 10:
			int_pr = CorbaErrorDescriptions.EXCPT_NE_COMM_LOSS.getPriority();
			break;
		case 11:
			int_pr = CorbaErrorDescriptions.EXCPT_CAPACITY_EXCEEDED
					.getPriority();
			break;
		case 12:
			int_pr = CorbaErrorDescriptions.EXCPT_ACCESS_DENIED.getPriority();
			break;
		case 13:
			int_pr = CorbaErrorDescriptions.EXCPT_TOO_MANY_OPEN_ITERATORS
					.getPriority();
			break;
		case 14:
			int_pr = CorbaErrorDescriptions.EXCPT_UNSUPPORTED_ROUTING_CONSTRAINTS
					.getPriority();
			break;
		case 15:
			int_pr = CorbaErrorDescriptions.EXCPT_USERLABEL_IN_USE
					.getPriority();
			break;
		default:
			break;
		}
		return int_pr;
	}

	public Throwable getException() {
		return prf;
	}

	public String printError() {
		int errorCode = prf.exceptionType.value();
		String str_error = "ProcessingFailureException: reason: "
				+ prf.errorReason + ", ";
		switch (errorCode) {
		case 0:
			str_error += CorbaErrorDescriptions.EXCPT_NOT_IMPLEMENTED
					.getErrorString();
			break;
		case 1:
			str_error += CorbaErrorDescriptions.EXCPT_INTERNAL_ERROR
					.getErrorString();
			break;
		case 2:
			str_error += CorbaErrorDescriptions.EXCPT_INVALID_INPUT
					.getErrorString();
			break;
		case 3:
			str_error += CorbaErrorDescriptions.EXCPT_OBJECT_IN_USE
					.getErrorString();
			break;
		case 4:
			str_error += CorbaErrorDescriptions.EXCPT_TP_INVALID_ENDPOINT
					.getErrorString();
			break;
		case 5:
			str_error += CorbaErrorDescriptions.EXCPT_ENTITY_NOT_FOUND
					.getErrorString();
			break;
		case 6:
			str_error += CorbaErrorDescriptions.EXCPT_TIMESLOT_IN_USE
					.getErrorString();
			break;
		case 7:
			str_error += CorbaErrorDescriptions.EXCPT_PROTECTION_EFFORT_NOT_MET
					.getErrorString();
			break;
		case 8:
			str_error += CorbaErrorDescriptions.EXCPT_NOT_IN_VALID_STATE
					.getErrorString();
			break;
		case 9:
			str_error += CorbaErrorDescriptions.EXCPT_UNABLE_TO_COMPLY
					.getErrorString();
			break;
		case 10:
			str_error += CorbaErrorDescriptions.EXCPT_NE_COMM_LOSS
					.getErrorString();
			break;
		case 11:
			str_error += CorbaErrorDescriptions.EXCPT_CAPACITY_EXCEEDED
					.getErrorString();
			break;
		case 12:
			str_error += CorbaErrorDescriptions.EXCPT_ACCESS_DENIED
					.getErrorString();
			break;
		case 13:
			str_error += CorbaErrorDescriptions.EXCPT_TOO_MANY_OPEN_ITERATORS
					.getErrorString();
			break;
		case 14:
			str_error += CorbaErrorDescriptions.EXCPT_UNSUPPORTED_ROUTING_CONSTRAINTS
					.getErrorString();
			break;
		case 15:
			str_error += CorbaErrorDescriptions.EXCPT_USERLABEL_IN_USE
					.getErrorString();
			break;
		default:
			str_error += "";
			break;
		}
		return str_error;
	}

	public CorbaErrorDescriptions.PRIORITY getPriority() {
		int errorCode = prf.exceptionType.value();
		CorbaErrorDescriptions.PRIORITY int_pr = CorbaErrorDescriptions.PRIORITY.MAJOR;
		switch (errorCode) {
		case 0:
			int_pr = CorbaErrorDescriptions.EXCPT_NOT_IMPLEMENTED.getPriority();
			break;
		case 1:
			int_pr = CorbaErrorDescriptions.EXCPT_INTERNAL_ERROR.getPriority();
			break;
		case 2:
			int_pr = CorbaErrorDescriptions.EXCPT_INVALID_INPUT.getPriority();
			break;
		case 3:
			int_pr = CorbaErrorDescriptions.EXCPT_OBJECT_IN_USE.getPriority();
			break;
		case 4:
			int_pr = CorbaErrorDescriptions.EXCPT_TP_INVALID_ENDPOINT
					.getPriority();
			break;
		case 5:
			int_pr = CorbaErrorDescriptions.EXCPT_ENTITY_NOT_FOUND
					.getPriority();
			break;
		case 6:
			int_pr = CorbaErrorDescriptions.EXCPT_TIMESLOT_IN_USE.getPriority();
			break;
		case 7:
			int_pr = CorbaErrorDescriptions.EXCPT_PROTECTION_EFFORT_NOT_MET
					.getPriority();
			break;
		case 8:
			int_pr = CorbaErrorDescriptions.EXCPT_NOT_IN_VALID_STATE
					.getPriority();
			break;
		case 9:
			int_pr = CorbaErrorDescriptions.EXCPT_UNABLE_TO_COMPLY
					.getPriority();
			break;
		case 10:
			int_pr = CorbaErrorDescriptions.EXCPT_NE_COMM_LOSS.getPriority();
			break;
		case 11:
			int_pr = CorbaErrorDescriptions.EXCPT_CAPACITY_EXCEEDED
					.getPriority();
			break;
		case 12:
			int_pr = CorbaErrorDescriptions.EXCPT_ACCESS_DENIED.getPriority();
			break;
		case 13:
			int_pr = CorbaErrorDescriptions.EXCPT_TOO_MANY_OPEN_ITERATORS
					.getPriority();
			break;
		case 14:
			int_pr = CorbaErrorDescriptions.EXCPT_UNSUPPORTED_ROUTING_CONSTRAINTS
					.getPriority();
			break;
		case 15:
			int_pr = CorbaErrorDescriptions.EXCPT_USERLABEL_IN_USE
					.getPriority();
			break;
		default:
			break;
		}
		return int_pr;
	}

}
