package com.help.android.utility

/**
 * Created by mymacbookpro on 2020-04-30
 * TODO: Add a class header comment!
 */
object ConstantAPI {
    val prod:String get() = ""

    val test:String get() = "https://reqres.in"

    val dev:String get() = "https://velectico.top"
}

///////////////////////////////////////////////////////////////////////

const val CONNECT_TIME_OUT:Long = 1
const val READ_TIME_OUT:Long = 15
const val WRITE_TIME_OUT:Long = 15

const val ERROR_CODE_UNKNOWN_HOST = 604
const val ERROR_CODE_TIME_OUT = 408
const val ERROR_CODE_OTHER = 600

///////////////////////////////////////////////////////////////////////

const val READ_ALL_RESOURCES_DATA = "ReadAllResourcesData"
const val READ_ALL_USERS_DATA = "ReadAllUsersData"
const val USER_LOGIN = "UserLogin"
const val PRODUCT_LIST = "ProductList"
const val EXPENSE_LIST = "ExpenseList"
const val MASTER_DATA_LIST = "MasterDataList"
const val EXPENSE_CREATE_EDIT = "CreateEditExpense"
const val EXPENSE_DELETE = "DeleteExpense"

const val COMPLAINT_CREATE = "Save_Complaint"

const val LEAVE_REASON = "LeaveReason"
const val LEAVE_APPLY = "ApplyLeave"
const val LEAVE_EDIT = "EditLeave"
const val LEAVE_DELETE = "DeleteLeave"
const val LEAVE_LIST = "ListLeave"



const val REQ_HEADER_TYPE_KEY = "Type"
const val REQ_HEADER_TYPE_VAL = "App"
const val REQ_HEADER_DEVICE_ID_KEY = "Userdeviceid"
const val REQ_HEADER_DEVICE_ID_VAL = "569966666"
const val REQ_HEADER_DEVICE_TYPE_KEY = "Devicetype"
const val REQ_HEADER_DEVICE_TYPE_VAL = "Android"
const val USER_LOGIN_MOBILE = "userMobile"
const val USER_LOGIN_PASSWORD = "userPassword"

const val USER_ID = "userId"
const val USER_ROLE = "userRole"
const val USER_NAME = "userName"
const val DROP_DOWN_NAME = "DM_Dropdown_Name"
//create expense params
const val BEAT_TASK_ID = "beatTaskId"
const val EXP_HEAD_ID = "Exp_Head_Id"
const val MIS_EXPENSE_AMOUNT = "misExpenseAmt"
const val APPLIED_ON_DATE = "appliedOnDate"
const val APPLIED_BY_USER_ID = "applieedByUserId"
const val FILE_TO_UPLOAD = "recPhoto"
const val EXPENSE_ID = "expenseId"

//create complaint params

const val COMPLAINT_TYPE = "complaintype"
const val DISTID = "CR_Distrib_ID"
const val DEALERID = "CR_Dealer_ID"
const val MECHANICID ="CR_Mechanic_ID"
const val QTY = "CR_Qty"
const val BATCHNO = "CR_Batch_no"
const val REMARKS = "CR_Remarks"
const val PHOTO = "recPhoto"
const val TASKID = "taskId"
const val PRODNAME = "prodName"

//Leave params
const val LEAVE_ID = "leaveId"
const val LEAVE_FROM = "leaveFromDate"
const val LEAVE_TO = "leaveToDate"
const val LEAVE_REASON_ID = "leaveReasonId"
const val LEAVE_REASON_OTHER = "leaveReasonOther"


const val GOOGLE_BASE_URL = "https://www.google.com"
const val ENDPOINT_GET_RATES = "/pranaycare/server/getRate.php"
const val ENDPOINT_GET_PHOTOS = "photos"
const val ENDPOINT_GET_RESOURCES = "/api/unknown"
const val ENDPOINT_GET_ALL_USERS = "/api/users"

const val APP_API_NAME = "https://cdn-api.co-vin.in/api/v2/auth/public/"

const val getOtp = APP_API_NAME+"generateOTP"
const val veryfyOtp = APP_API_NAME+"confirmOTP"


const val products_categories = APP_API_NAME+"products/categories"
const val product = APP_API_NAME+"products"

const val customer_register = APP_API_NAME+"customers"
const val customer_login = APP_API_NAME+"customers"
const val customer_update_address = APP_API_NAME+"customers"
const val customer_retrieve = APP_API_NAME+"customers"
const val customer_order_retrieve = APP_API_NAME+"orders"
const val customer_wishList_retrieveById = APP_API_NAME+"wishlist/get_by_user"
const val getwishListProductByKey = APP_API_NAME+"wishlist"