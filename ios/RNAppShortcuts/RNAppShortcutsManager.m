//
//  RNAppShortcutsManager.m
//  RNAppShortcuts
//
//  Created by Rodolfo Agapito Sarabó on 10/10/24.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>
#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTUtils.h>
#import "RNAppShortcutsManager.h"

NSString *const RCTShortcutItemClicked = @"ShortcutItemClicked";

NSDictionary *RNAppShortcut(UIApplicationShortcutItem *item) {
    if (!item) return nil;
    return @{
        @"type": item.type,
        @"title": item.localizedTitle,
        @"userInfo": item.userInfo ?: @{}
    };
}

@implementation RNAppShortcutsManager
{
    UIApplicationShortcutItem *_initialAction;
}

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (instancetype)init
{
    if ((self = [super init])) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleShortcutItemPress:)
                                                     name:RCTShortcutItemClicked
                                                   object:nil];
    }
    return self;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setBridge:(RCTBridge *)bridge
{
    _bridge = bridge;
    _initialAction = [bridge.launchOptions[UIApplicationLaunchOptionsShortcutItemKey] copy];
}

// Map user passed array of UIApplicationShortcutItem
- (NSArray*)dynamicShortcutItemsForPassedArray:(NSArray*)passedArray {
    // FIXME: Dynamically map icons from UIApplicationShortcutIconType to / from their string counterparts
    // so we don't have to update this list every time Apple adds new system icons.
    NSDictionary *icons = @{
        @"Compose": @(UIApplicationShortcutIconTypeCompose),
        @"Play": @(UIApplicationShortcutIconTypePlay),
        @"Pause": @(UIApplicationShortcutIconTypePause),
        @"Add": @(UIApplicationShortcutIconTypeAdd),
        @"Location": @(UIApplicationShortcutIconTypeLocation),
        @"Search": @(UIApplicationShortcutIconTypeSearch),
        @"Share": @(UIApplicationShortcutIconTypeShare),
        @"Prohibit": @(UIApplicationShortcutIconTypeProhibit),
        @"Contact": @(UIApplicationShortcutIconTypeContact),
        @"Home": @(UIApplicationShortcutIconTypeHome),
        @"MarkLocation": @(UIApplicationShortcutIconTypeMarkLocation),
        @"Favorite": @(UIApplicationShortcutIconTypeFavorite),
        @"Love": @(UIApplicationShortcutIconTypeLove),
        @"Cloud": @(UIApplicationShortcutIconTypeCloud),
        @"Invitation": @(UIApplicationShortcutIconTypeInvitation),
        @"Confirmation": @(UIApplicationShortcutIconTypeConfirmation),
        @"Mail": @(UIApplicationShortcutIconTypeMail),
        @"Message": @(UIApplicationShortcutIconTypeMessage),
        @"Date": @(UIApplicationShortcutIconTypeDate),
        @"Time": @(UIApplicationShortcutIconTypeTime),
        @"CapturePhoto": @(UIApplicationShortcutIconTypeCapturePhoto),
        @"CaptureVideo": @(UIApplicationShortcutIconTypeCaptureVideo),
        @"Task": @(UIApplicationShortcutIconTypeTask),
        @"TaskCompleted": @(UIApplicationShortcutIconTypeTaskCompleted),
        @"Alarm": @(UIApplicationShortcutIconTypeAlarm),
        @"Bookmark": @(UIApplicationShortcutIconTypeBookmark),
        @"Shuffle": @(UIApplicationShortcutIconTypeShuffle),
        @"Audio": @(UIApplicationShortcutIconTypeAudio),
        @"Update": @(UIApplicationShortcutIconTypeUpdate)
    };

    NSMutableArray *shortcutItems = [NSMutableArray new];

    [passedArray enumerateObjectsUsingBlock:^(NSDictionary *item, NSUInteger idx, BOOL *stop) {
        NSString *iconName = item[@"icon"];

        // If passed iconName is enum, use system icon
        // Otherwise, load from bundle
        UIApplicationShortcutIcon *shortcutIcon;
        NSNumber *iconType = icons[iconName];

        if (iconType) {
            shortcutIcon = [UIApplicationShortcutIcon iconWithType:[iconType intValue]];
        } else if (iconName) {
            shortcutIcon = [UIApplicationShortcutIcon iconWithTemplateImageName:iconName];
        }

        [shortcutItems addObject:[[UIApplicationShortcutItem alloc] initWithType:item[@"type"]
                                                                  localizedTitle:item[@"title"] ?: item[@"type"]
                                                               localizedSubtitle:item[@"subtitle"]
                                                                            icon:shortcutIcon
                                                                        userInfo:item[@"userInfo"]]];
    }];

    return shortcutItems;
}

RCT_EXPORT_METHOD(setShortcutItems:(NSArray *) shortcutItems)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSArray *dynamicShortcuts = [self dynamicShortcutItemsForPassedArray:shortcutItems];
    [UIApplication sharedApplication].shortcutItems = dynamicShortcuts;
  });
}

RCT_EXPORT_METHOD(isSupported:(RCTResponseSenderBlock)callback)
{
    BOOL supported = [[UIApplication sharedApplication].delegate.window.rootViewController.traitCollection forceTouchCapability] == UIForceTouchCapabilityAvailable;

    callback(@[[NSNull null], [NSNumber numberWithBool:supported]]);
}

RCT_EXPORT_METHOD(clearShortcutItems)
{
    [UIApplication sharedApplication].shortcutItems = nil;
}

+ (void)onShortcutItemPress:(UIApplicationShortcutItem *) shortcutItem completionHandler:(void (^)(BOOL succeeded)) completionHandler
{
    RCTLogInfo(@"[RNAppShortcuts] Quick action shortcut item pressed: %@", [shortcutItem type]);

    [[NSNotificationCenter defaultCenter] postNotificationName:RCTShortcutItemClicked
                                                        object:self
                                                      userInfo:RNAppShortcut(shortcutItem)];

    completionHandler(YES);
}

- (void)handleShortcutItemPress:(NSNotification *) notification
{
    [_bridge.eventDispatcher sendDeviceEventWithName:@"appShortcuts"
                                                body:notification.userInfo];
}

- (NSDictionary *)constantsToExport
{
    return @{
      @"initialAction": RCTNullIfNil(RNAppShortcut(_initialAction))
    };
}

@end
