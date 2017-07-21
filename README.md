Part 1: To solve offline payments when the User is offline (neither network nor
data)
Solution :
• User can generate QR code by simply entering the amount to be paid on their
phone.
• The generated QR code’s encrypted data will contain User’s ID [which is actually
their User ID generated and used by firebase cloud services] and the amount to be
paid to the Merchant.
• All the merchant needs to do now is to scan this QR code and voila! Through the
merchant’s internet access, the payment is complete and changes will be reflected
in firebase (cloud) database as well!
• Note that the payment will be aborted in case the User’s balance falls short of the
amount to be paid.
• Notification for successful payment will be sent to both whenever they connect to
the internet.

Part 2 : To solve offline payments when the User is in network but no access to
data and the Merchant is offline.
Solution :
• Inspired by Indian Governments initiative on offline mobile banking, use of USSD
codes for sending encrypted User ID of Merchant and User with the amount to be
paid could have been a great option.
• But, since we do not have a USSD code for the same, our prototype will just send
encrypted messages to a number connected to the server which will implement
the transaction.
• Further both the user and the merchant will receive conformation if the transaction
is successful.(Whenever they come online)

Additional problem : To solve offline payments when the User is online but
Merchant is offline (neither network nor data).
Solution :
• Merchant can generate QR code by simply entering the amount due to them on
their phone.
• The generated QR code’s encrypted data will contain Merchant’s ID [which is
actually their User ID generated and used by firebase cloud services] and the
amount due to the Merchant.
• All the user needs to do now is to scan this QR code and confirm the amount they
need to pay. Now, through the User’s internet access, the payment is complete
and changes will be reflected in firebase (cloud) database as well!
• Note that the payment will be aborted in case the User’s balance falls short of the
amount to be paid.

• Notification for successful payment will be sent to both whenever they connect to
the internet.

EASE TO THE END USERS

• Users don’t need to remember their unique user ID used to generate QR code, Just
enter the amount and a unique QR code is generated. This is because database is also
stored locally in user’s phone which allows the user or merchant to generate QR code
with ease.
• Why QR code? Why not Bluetooth or Hotspot (local WLAN)?
QR codes were a deliberate choice. This is because in case of Hotspots (local
WLAN) in windows phones, iPhones and even Android N, mobile applications cannot
activate hotspots. It needs to be done manually. And in case of Bluetooth, since a gadget
can only be paired with 1 bluetooth device (for now), it can be really frustrating at times.
• To finally process transaction you just need to scan the code and automatically money is
transferred from user’s wallet to merchant’s wallet.

SECURITY

• This app features layers of encryption which makes it very hard for anyone to do
anything we don't want you to do.
• First of all, our firebase database can only be accessed from our app. Secondly,
the user’s ID is also encrypted and generated by firebase itself. Thirdly, before
encrypting our user ID into a QR code, we further encrypt the ID. This way no one
with our QR code can extract anyone’s firebase user ID.
• QR Codes are small harmless patterns and their intent is only to help you get data from a
printed medium to a digital medium. They cannot be read with the naked eye so they are

particularly difficult to manipulate. QR Codes are only meant to be machine readable. This
means that a human looking at the code is unable to determine its content.
• Security in Firebase is handled by setting JSON like object inside security rules.
• Also in following solution QR code is generated using a unique user UID which is
unknown to the merchant which makes it very safe as merchant can never regenerate
user’s QR code to make false transactions.
• User is notified whenever any transaction is made related to his or her account.
