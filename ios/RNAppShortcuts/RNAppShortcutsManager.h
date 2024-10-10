//
//  RNAppShortcutsManager.h
//  RNAppShortcuts
//
//  Created by Rodolfo Agapito Sarabó on 10/10/24.
//

#import <React/RCTBridgeModule.h>

@interface RNAppShortcutsManager : NSObject <RCTBridgeModule>
+(void) onQuickActionPress:(UIApplicationShortcutItem *) shortcutItem completionHandler:(void (^)(BOOL succeeded)) completionHandler;
@end
