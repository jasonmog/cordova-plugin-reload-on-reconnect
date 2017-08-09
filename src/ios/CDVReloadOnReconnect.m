#import "CDVReloadOnReconnect.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import "CDVReachability.h"
#import "CDVConnection.h"
#import <Cordova/CDVViewController.h>

@implementation CDVReloadOnReconnect {
    CDVConnection *_connection;
    BOOL _isConnected;
}

- (void)pluginInitialize {
    _connection = [(CDVViewController *)self.viewController getCommandInstance:@"NetworkStatus"];
    _isConnected = ![_connection.connectionType isEqualToString:@"none"];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateConnectionType:)
                                                 name:kReachabilityChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateConnectionType:)
                                                 name:CTRadioAccessTechnologyDidChangeNotification object:nil];
}

- (void)updateConnectionType:(NSNotification *)note {
    if ([_connection.connectionType isEqualToString:@"none"]) {
        _isConnected = NO;
    } else if (!_isConnected) {
        _isConnected = YES;
        
        [(UIWebView *)self.webView reload];
    }
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
