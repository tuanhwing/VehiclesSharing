const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addMessage = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const original = req.query.text;
  // Push it into the Realtime Database then send a response
  admin.database().ref('/messages').push({original: original}).then(snapshot => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    res.redirect(303, snapshot.ref);
  });
});


exports.demo = functions.https.onRequest((req, res) => {
	var lat = req.query.lat;
	var long = req.query.long;
	var usersRef = admin.database().ref('requests_needer');
	usersRef.once('value', function(snapshot) {
		// console.log(snapshot.val());
		var i = 0;
		snapshot.forEach(function(reqSnapshot){
			console.log('in position %d - %s - %s', i++, reqSnapshot.key,reqSnapshot.graberDiviceId);
			// if(getDistanceFromLatLonInKm(lat,long,reqSnapshot.val().locationRequest.locationLat,reqSnapshot.val().locationRequest.locationLong) > 0)
		});
		res.status(200).send(snapshot.val());
	});
  
});

// Listens for new messagzzes added to /messages/:pushId/original and creates an
// uppercase version of the message to /messages/:pushId/uppercase
exports.makeUppercase = functions.database.ref('/messages/{pushId}/original')
    .onWrite(event => {
      // Grab the current value of what was written to the Realtime Database.
      const original = event.data.val();
      console.log('Uppercasing', event.params.pushId, original);
      const uppercase = original.toUpperCase();

       const tokenId = "ejXJ8krZE50:APA91bF5h-eWMynEki3rXxwTeyN0aAYHmMh7YGvGWsNQuqVQDS11aIwRZIy8G-rEE8XbqyW79LZaCwE68AALXzDkmewj8lqWE503jxsW5TOJnFBjKY4u8ia42v8-7Ts1oUX9dBNMYn26";

      const payload = {
                notification: {
                    title: "Demo notification",
                    body: uppercase,
                }
            };

             admin.messaging().sendToDevice(tokenId, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });


      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return event.data.ref.parent.child('uppercase').set(uppercase);
    });

// exports.sendNotification = functions.database.ref('/notifications/messages/{pushId}')
//     .onWrite(event => {
//         const message = event.data.current.val();
//         const senderUid = message.from;
//         const receiverUid = message.to;
//         const promises = [];

//         if (senderUid == receiverUid) {
//             //if sender is receiver, don't send notification
//             promises.push(event.data.current.ref.remove());
//             return Promise.all(promises);
//         }

//         const getInstanceIdPromise = admin.database().ref(`/users/${receiverUid}/instanceId`).once('value');
//         const getReceiverUidPromise = admin.auth().getUser(receiverUid);

//         return Promise.all([getInstanceIdPromise, getReceiverUidPromise]).then(results => {
//             const instanceId = results[0].val();
//             const receiver = results[1];
//             console.log('notifying ' + receiverUid + ' about ' + message.body + ' from ' + senderUid);

//             const payload = {
//                 notification: {
//                     title: receiver.displayName,
//                     body: message.body,
//                     icon: receiver.photoURL
//                 }
//             };

//             admin.messaging().sendToDevice(instanceId, payload)
//                 .then(function (response) {
//                     console.log("Successfully sent message:", response);
//                 })
//                 .catch(function (error) {
//                     console.log("Error sending message:", error);
//                 });
//         });
//     });

exports.inforequest = functions.database.ref('/requests_needer/{pushId}').onWrite(
	event =>{
		const original = event.data.val();
		console.log('demo_inforequest_location', event.params.pushId, original, original.locationRequest.locationLat, original.locationRequest.locationLong);
		const payload = {
                notification: {
                    title: "Demo notification",
                    body: "You add a new request",
                }
            };

        admin.messaging().sendToDevice(original.graberDiviceId, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });

		return event.data.ref.child('uppercase').set("uppercase");
	});

