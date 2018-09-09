# dynabiz-core

### All build-in business exceptions 
| Code | Message |
| ------ | ------ |
1001 | LoginException:Wrong password
1002 | LoginException:Wrong username
1003 | LoginException:Wrong status
2001 | RegisterException:Illegal format of password
2002 | RegisterException:Illegal format of email
2003 | RegisterException:Illegal format of phone
2004 | RegisterException:Illegal format of user name
2005 | RegisterException:Email already exists
2006 | RegisterException:Phone already exists
2007 | RegisterException:User name already exists
3001 | PermissionException:No permission
3002 | PermissionException:Permission not found
4001 | TokenException:Bad token
4002 | TokenException:Token not found
6001 | FinanceException:Auth Error
6002 | FinanceException:Network Error
6003 | FinanceException:Reached Limit
6004 | FinanceException:Illegal Operation
6005 | FinanceException:Insufficient Balance
7001 | VerificationCodeException:wrong verification code
8001 | FileException:File size is beyond the limit
8002 | FileException:File size is too small
8003 | FileException:File not found
8004 | FileException:Create file failed
8005 | FileException:Format of file is unexpected
