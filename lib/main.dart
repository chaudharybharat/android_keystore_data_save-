import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp( MyApp());
}

class MyApp extends StatelessWidget {

   const MyApp({Key key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
    MyHomePage({Key key}) : super(key: key);
   String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel("example.com/channel");

  _MyHomePageState() {
    platform.setMethodCallHandler(_methodHandler);
  }
String plainTextResult="";
  TextEditingController plainTextController=TextEditingController();
  TextEditingController encyptedTextController=TextEditingController();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Android KeyStore Demo"),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
        Container(
          margin: EdgeInsets.symmetric(vertical: 10,horizontal: 20),
          child: TextField (
            controller: plainTextController,
          decoration: InputDecoration(
              border: OutlineInputBorder(),
              labelText: 'Enter plain text',
              hintText: 'Enter plain text'
          ),
      ),
        ),
            MaterialButton(
              child: Text("Encrypt Data"),
              onPressed: _encyptedTapButton,
              color: Colors.grey,
            ),
            Container(
              margin: EdgeInsets.symmetric(vertical: 10,horizontal: 20),
              child: TextField (
                controller: encyptedTextController,
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Enter encypted text',
                    hintText: 'Enter encypted text'
                ),
              ),
            ),


            MaterialButton(
              child: Text("Decrypt Data"),
              onPressed: _decryptTapButton,
              color: Colors.grey,
            ),
            SizedBox(height: 10,),
            Text(
              'Generate Plain Text : ${plainTextResult}',
            ),
          ],
        ),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }

  Future<Null> _encyptedTapButton() async {
    if(plainTextController.text.isNotEmpty){
      final Map<String, dynamic> params = {'plain_text':plainTextController.text};
      var result = await platform.invokeMethod("encypted",params);

      encyptedTextController.text=result;
      setState(() {
      });
      debugPrint("=====${result}==");
    }

  }
  Future<Null> _decryptTapButton() async {
    if(encyptedTextController.text.isNotEmpty){
      final Map<String, dynamic> params = {'encypte_text': encyptedTextController.text};
      var result = await platform.invokeMethod("decrypt",params);

     plainTextResult=result;
      setState(() {
      });
      debugPrint("=====${result}==");
    }

  }

  Future<dynamic> _methodHandler(MethodCall call) async {
    switch (call.method) {
      case "message":
        debugPrint(call.arguments);
        return new Future.value("");
    }
  }
}