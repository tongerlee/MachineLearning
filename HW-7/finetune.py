import torch
import torch.nn as nn
import torch.optim as optim
from PIL import Image
import pandas as pd
from torchvision import transforms, models
from torch.utils.data import Dataset, DataLoader
import time
import os


class LandmarksDataset(Dataset):

    def __init__(self, csv_file, root_dir, transform):

        self.landmarks_frame = pd.read_csv(csv_file)
        self.root_dir = root_dir
        self.transform = transform

    def __len__(self):
        return len(self.landmarks_frame)

    def __getitem__(self, idx):
        img_name = os.path.join(self.root_dir,
                                self.landmarks_frame.iloc[idx, 1])
        image = Image.open(img_name + '.jpg')
        landmark_output = self.landmarks_frame.iloc[idx, 3]

        image_trans = self.transform(image)

        return image_trans, landmark_output


class LandmarksDatasetTest(Dataset):

    def __init__(self, csv_file, root_dir, transform):

        self.landmarks_frame = pd.read_csv(csv_file)
        self.root_dir = root_dir
        self.transform = transform

    def __len__(self):
        return len(self.landmarks_frame)

    def __getitem__(self, idx):
        img_name = os.path.join(self.root_dir,
                                self.landmarks_frame.iloc[idx, 1])
        image = Image.open(img_name + '.jpg')

        if self.transform:
            image = self.transform(image)

        return image


train_dataset = LandmarksDataset(csv_file='hw7data/train.csv',
                                 root_dir='hw7data/images/',
                                 transform=transforms.Compose([
                                     transforms.RandomResizedCrop(224),
                                     transforms.RandomHorizontalFlip(),
                                     transforms.ToTensor(),
                                     transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
                                 ])
                                 )


dl_train = DataLoader(train_dataset, batch_size=64,
                      shuffle=True, num_workers=0)


device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
num_classes_tmp = 10
num_epochs_tmp = 3
feature_extract_tmp = True


def train_model(model, data_in, criterion, optimizer, scheduler, num_epochs):
    since = time.time()

    for epoch in range(num_epochs):
        print('Epoch {}/{}'.format(epoch, num_epochs - 1))
        print('-' * 10)

        scheduler.step()
        model.train()  # Set model to training mode
        running_loss = 0.0
        running_corrects = 0

        # Iterate over data.
        for inputs, labels in data_in:
            inputs = inputs.to(device)
            labels = labels.to(device)

            optimizer.zero_grad()

            # forward
            with torch.set_grad_enabled(True):
                outputs = model(inputs)
                _, preds = torch.max(outputs, 1)
                loss = criterion(outputs, labels)

            loss.backward()
            optimizer.step()

            running_loss += loss.item() * inputs.size(0)
            running_corrects += torch.sum(preds == labels.data)

        epoch_loss = running_loss / len(train_dataset)
        epoch_acc = running_corrects.double() / len(train_dataset)

        print('Loss: {:.4f} Acc: {:.4f}'.format(epoch_loss, epoch_acc))

    print()

    time_elapsed = time.time() - since
    print('Training complete in {:.0f}m {:.0f}s'.format(time_elapsed // 60, time_elapsed % 60))

    return model


model_ft = models.resnet18(pretrained=True)
num_ftrs = model_ft.fc.in_features
model_ft.fc = nn.Linear(num_ftrs, num_classes_tmp)
model_ft = model_ft.to(device)

optimizer_ft = optim.SGD(model_ft.parameters(), lr=0.001, momentum=0.9)

# Setup the loss function
criterion_tmp = nn.CrossEntropyLoss()

exp_lr_scheduler = optim.lr_scheduler.StepLR(optimizer_ft, step_size=7, gamma=0.1)

# Train
print('##### Training begins here #####')

model_ft = train_model(model_ft, dl_train, criterion_tmp, optimizer_ft, exp_lr_scheduler, num_epochs_tmp)

print('##### Training ends here #####')
print()

test_dataset = LandmarksDatasetTest(csv_file='hw7data/test.csv',
                                    root_dir='hw7data/images',
                                    transform=transforms.Compose([
                                        transforms.Resize(256),
                                        transforms.CenterCrop(224),
                                        transforms.ToTensor(),
                                        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
                                        ])
                                 )

dl_test = DataLoader(test_dataset)


def test_model(model, dl_in):
    since_test = time.time()

    # Each epoch has a training and validation phase
    model.eval()  # Set model to evaluation mode
    # Iterate over data.
    for inputs in dl_in:
        inputs = inputs.to(device)
        outputs = model(inputs)
        _, preds = torch.max(outputs, 1)
        f.write(str(preds.item()) + '\n')

    time_elapsed = time.time() - since_test
    print('Testing complete in {:.0f}m {:.0f}s'.format(time_elapsed // 60, time_elapsed % 60))
    print('###### Test ends here ######')
    return


print('###### Test begins here ######')

f = open("submission.txt", "w")
f.write('landmark_id\n')
test_model(model_ft, dl_test)
f.close()


