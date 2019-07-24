import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Step Detection'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  //static const MethodChannel _mChannel = const MethodChannel('step_recognition/messages');
  static const EventChannel _eChannelSteps = const EventChannel("step_recognition/events-steps");

  static Stream<int> _streamSteps;

  int _stepCount = 0;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'You stepped this many times:',
            ),
            StreamBuilder(
              initialData: 0,
              stream: getStepsStream(),
              builder: (BuildContext context, AsyncSnapshot snapshot) {
                if (snapshot.hasData) _stepCount += snapshot.data;
                return Text(
                  '$_stepCount',
                  style: Theme.of(context).textTheme.display1,
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  static Stream<int> getStepsStream() {
    if (_streamSteps == null) {
      _streamSteps = _eChannelSteps.receiveBroadcastStream().cast<int>();
    }
    return _streamSteps;
  }
}
