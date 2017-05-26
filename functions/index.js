'use strict';
const functions = require('firebase-functions');

const admin = require('firebase-admin');
const distance = require('./distance');
// const express = require('express');
// const app = express();
admin.initializeApp(functions.config().firebase);


// app.post('api/request_post', (req,res) => {
// 	// const info = req.body.info;
// 	// console.log(info);
// 	console.log(req);
// 	res.status(200);
// })

// app.post('api/request_get', (req,res) => {
// 	// const info = req.body.info;
// 	// console.log(info);
// 	console.log(req);
// 	res.status(200).send("Hello");
// })
// // Expose the API as a function
// exports.api = functions.https.onRequest(app);

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

exports.getinfo_requestgraber = functions.https.onRequest((req,res) => {
	var info = req.query.requestId;
	console.log(info);
	var requestRef = admin.database().ref('requests_needer');
	requestRef.once('value', function(snapshot){
		res.status(200).send(snapshot.val());
	});
});


exports.distance = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const lat1 = req.body.lat1;
  const lat2 = req.body.lat2;
  const long1 = req.body.long1;
  const long2 = req.body.long2;
  // Push it into the Realtime Database then send a response
  res.status(200).json({error: false, distance: distance(lat1,long1,lat2,long2)});
});

exports.demo = functions.https.onRequest((req, res) => {
	var lat = req.query.lat;
	var long = req.query.long;
	var usersRef = admin.database().ref('requests_needer');
	usersRef.once('value', function(snapshot) {
		// console.log(snapshot.val());
		var i = 0;
		snapshot.forEach(function(reqSnapshot){
			console.log('in position %d - %s', i++, reqSnapshot.key);
			console.log(reqSnapshot.val().graberDiviceId);
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




//Add all graber nearly needer to show them in google map into needer
exports.nearlyrequestneeder = functions.database.ref('/requestfromneeder/{pushId}').onWrite(
	event => {
		var pushId = event.params.pushId;
		const value = event.data.val();
		var requestsNeederRef = admin.database().ref('requestfromgraber');
		requestsNeederRef.once('value', function(snapshot) {
		// console.log(snapshot.val());
			var i = 0;
			snapshot.forEach(function(reqSnapshot){
				var key = reqSnapshot.key;
				if(pushId.localeCompare(key) != 0){//Returns 0 if the two strings are equal
					admin.database().ref('/thegraber_near_needer').child(pushId).push(reqSnapshot.val()).then(snapshot => {
						console.log(reqSnapshot.val());
			  		});

				// if(getDistanceFromLatLonInKm(lat,long,reqSnapshot.val().locationRequest.locationLat,reqSnapshot.val().locationRequest.locationLong) > 0)
				}
			});
		});
	});

//Add all needer nearly graber to show them in google map into graber
exports.nearlyrequestgraber = functions.database.ref('/requestfromgraber/{pushId}').onWrite(
	event => {
		var pushId = event.params.pushId;
		const value = event.data.val();
		var requestsNeederRef = admin.database().ref('requestfromneeder');
		requestsNeederRef.once('value', function(snapshot) {
		// console.log(snapshot.val());
			var i = 0;
			snapshot.forEach(function(reqSnapshot){
				var key = reqSnapshot.key;
				if(pushId.localeCompare(key) != 0){//Returns 0 if the two strings are equal
					admin.database().ref('/theneeder_near_graber').child(pushId).push(reqSnapshot.val()).then(snapshot => {

			  		});

				// if(getDistanceFromLatLonInKm(lat,long,reqSnapshot.val().locationRequest.locationLat,reqSnapshot.val().locationRequest.locationLong) > 0)
				}
			});
		});
	});

